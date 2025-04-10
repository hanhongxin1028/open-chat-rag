package cn.alchemylab.chatrag;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
class RAGEmbeddingConfigTest {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private TokenTextSplitter tokenTextSplitter;

    @Autowired
    private SimpleVectorStore simpleVectorStore;

    @Autowired
    private PgVectorStore pgVectorStore;

    @Test
    public void simpleVectorStore_upload() {
        TikaDocumentReader reader = new TikaDocumentReader("./data/test.md");
        List<Document> documents = reader.get();
        List<Document> documentSplitterList = tokenTextSplitter.apply(documents);

        documents.forEach(doc -> doc.getMetadata().put("knowledge", "服务器账号密码"));

        documentSplitterList.forEach(doc -> doc.getMetadata().put("knowledge", "服务器账号密码"));

        simpleVectorStore.accept(documentSplitterList);

        log.info("上传完成");
    }

    @Test
    public void pgVectorStoreUpload() {
        // 文档读取 —— (TikaDocumentReader是基于Apache Tika的文档读取器，可以处理多种格式的文档)
        TikaDocumentReader reader = new TikaDocumentReader("./data/test.md");
        List<Document> documents = reader.get();

        // 使用 TokenTextSplitter 对文档内容进行分割
        List<Document> documentSplitterList = tokenTextSplitter.apply(documents);


        //        documents.forEach(doc -> doc.getMetadata().put("knowledge", "服务器账号密码"));


        documentSplitterList.forEach(doc -> doc.getMetadata().put("knowledge", "服务器账号密码"));

        pgVectorStore.accept(documentSplitterList);

        log.info("上传完成");
    }

    @Test
    public void chat() {
        String message = "韩泓鑫的用户名和密码是什么？";
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
                .filterExpression("knowledge == '服务器账号密码'") // 设置过滤条件
                .build();        // 构建SearchRequest对象

        List<Document> documents = pgVectorStore.similaritySearch(request);
        String documentCollectors = documents.stream().map(Document::getText).collect(Collectors.joining());

        Message ragMessage = new SystemPromptTemplate(SYSTEM_PROMPT).createMessage(Map.of("documents", documentCollectors));
        List<Message> messages = new ArrayList<>();
        messages.add(new UserMessage(message));
        messages.add(ragMessage);

        ChatClient.CallResponseSpec call = chatClient.prompt().messages(messages).call();

        String responseText = call.content();

        log.info("测试结果：{}", responseText);
    }

}
