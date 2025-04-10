package cn.alchemylab.chatrag.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Data
public class FileUploadRequest {

    private String filePath;

    private String knowledgeType; // 自定义知识分类，如"服务器账号密码"

}
