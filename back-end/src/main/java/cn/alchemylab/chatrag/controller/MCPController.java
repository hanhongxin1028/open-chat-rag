package cn.alchemylab.chatrag.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/mcp")
public class MCPController {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ToolCallbackProvider tools;

    @GetMapping("/generate_stream")
    public Flux<String> generate_stream(@RequestParam("userPrompt") String userPrompt){
        String systemPrompt = """
            You are an intelligent assistant that can call system tools to complete specific requests from users.
            When the user gives the operation request, you need to choose the right tool and fill in the exact parameters.
            """;


        return chatClient
                .prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .tools(tools)
                .stream()
                .content();
    }
}
