package cn.alchemylab.chatrag.service.impl;


import cn.alchemylab.chatrag.service.IRagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RagServiceImpl implements IRagService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private TokenTextSplitter tokenTextSplitter;

    @Autowired
    private PgVectorStore pgVectorStore;
    @Override
    public int pgVectorStoreUpload(FileSystemResource fileSystemResource, String knowledgeType) {

            // 1. 读取文档
            TikaDocumentReader reader = new TikaDocumentReader(fileSystemResource);
            List<Document> documents = reader.get();

            // 2. 分割文本
            List<Document> documentSplitterList = tokenTextSplitter.apply(documents);

            // 3. 打标签
            documentSplitterList.forEach(doc -> doc.getMetadata().put("knowledge", knowledgeType));

            // 4. 存储到向量数据库
            pgVectorStore.accept(documentSplitterList);

            log.info("分割为 {} 个块",
                    documentSplitterList.size());

            return documentSplitterList.size();

    }

    @Override
    public Flux<String> chatKonwledge(String message, String knowledgeType) {

        String SYSTEM_PROMPT = """
            Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
            If unsure, simply state that you don't know.
            Another thing you need to note is that your reply must be in Chinese!
            DOCUMENTS:
                {documents}
            """;
        // 指定文档搜索
        SearchRequest request = SearchRequest.builder()
                .query(message)  // 设置查询字符串为用户的消息
                .topK(5)         // 设置返回的文档数量为5
                .filterExpression(String.format("knowledge == '%s'", knowledgeType)) // 设置过滤条件
                .build();        // 构建SearchRequest对象

        List<Document> documents = pgVectorStore.similaritySearch(request);
        String documentCollectors = documents.stream().map(Document::getText).collect(Collectors.joining());

        Message ragMessage = new SystemPromptTemplate(SYSTEM_PROMPT).createMessage(Map.of("documents", documentCollectors));
        List<Message> messages = new ArrayList<>();
        messages.add(new UserMessage(message));
        messages.add(ragMessage);


        return chatClient
                .prompt()
                .messages(messages)
                .stream()
                .content();
    }
}
