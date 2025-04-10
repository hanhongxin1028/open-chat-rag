package cn.alchemylab.chatrag.controller;

import cn.alchemylab.chatrag.dto.Result;
import cn.alchemylab.chatrag.service.IRagService;
import jakarta.annotation.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/rag")
public class RagController {

    @Resource
    private  IRagService ragService;

    /**
     * 上传文件，embedding 到向量数据库中
     * @param file
     * @param knowledgeType
     * @return
     */
    @PostMapping("/uploadKnowledge")
    public Result uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart("knowledgeType") String knowledgeType
    ){
        try{
            // 临时保存文件
            Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);
            FileSystemResource fileSystemResource = new FileSystemResource(tempFile.toFile());

            //
            int chunkCount  = ragService.pgVectorStoreUpload(fileSystemResource, knowledgeType);

            Files.deleteIfExists(tempFile);

            return Result.ok(chunkCount);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }

    }

    @GetMapping("/chatKnowledge")
    public Flux<String> chatKnowledge(
            @RequestParam("message") String message,
            @RequestParam("knowledgeType") String knowledgeType
    ){


        return ragService.chatKonwledge(message, knowledgeType);

    }




}

