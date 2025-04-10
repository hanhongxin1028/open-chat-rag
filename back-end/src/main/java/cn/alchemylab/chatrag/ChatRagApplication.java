package cn.alchemylab.chatrag;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.alchemylab.chatrag.mapper")
public class ChatRagApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatRagApplication.class, args);
    }

}
