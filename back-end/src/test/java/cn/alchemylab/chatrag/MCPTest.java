package cn.alchemylab.chatrag;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class MCPTest {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ToolCallbackProvider tools;

    @Test
    public void test_tool() {
        String userInput = "有哪些工具可以使用";

        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).tools(tools).call().content());
    }

    @Test
    public void test_workflow() {
        String systemPrompt = """
            You are an intelligent assistant that can call system tools to complete specific requests from users.
            When the user gives the operation request, you need to choose the right tool and fill in the exact parameters.
            """;

        String userPrompt = "在 C:\\Users\\han\\Desktop\\电脑.txt 中写入诗歌《静夜思》";


        System.out.println("\n>>> QUESTION: " + userPrompt);
        System.out.println("\n>>> ASSISTANT: " + chatClient
                .prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .tools(tools)
                .call()
                .content()
        );
    }
}
