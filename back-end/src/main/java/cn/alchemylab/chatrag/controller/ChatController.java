package cn.alchemylab.chatrag.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatClient chatClient;

    /**
     * 模型流式回复
     *   http://localhost:8080/chat/generate_stream?message=你好
     * @param message 用户发送的信息
     */
    @GetMapping("/generate_stream")
    public Flux<String> generate_stream(@RequestParam("message") String message){

        return chatClient
                .prompt()
                .user(message)
                .stream()
                .content();
    }
}
