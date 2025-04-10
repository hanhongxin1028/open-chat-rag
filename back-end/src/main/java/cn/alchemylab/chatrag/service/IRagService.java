package cn.alchemylab.chatrag.service;

import org.springframework.core.io.FileSystemResource;
import reactor.core.publisher.Flux;

import java.nio.file.Path;

public interface IRagService {
    int pgVectorStoreUpload(FileSystemResource fileSystemResource, String knowledgeType);

    Flux<String> chatKonwledge(String message, String knowledgeType);
}
