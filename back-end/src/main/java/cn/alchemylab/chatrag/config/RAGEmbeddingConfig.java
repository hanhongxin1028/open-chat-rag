package cn.alchemylab.chatrag.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class RAGEmbeddingConfig {

    // 通过构造函数注入 EmbeddingModel
    private final EmbeddingModel embeddingModel;

    public RAGEmbeddingConfig(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
    }



    @Bean
    public SimpleVectorStore simpleVectorStore() {
        // 直接使用 EmbeddingModel
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean
    public PgVectorStore pgVectorStore(JdbcTemplate jdbcTemplate) {
        // 使用 EmbeddingModel 初始化 PgVectorStore
        return PgVectorStore.builder(jdbcTemplate,embeddingModel).build();
    }

}
