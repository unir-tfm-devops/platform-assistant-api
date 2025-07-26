package com.demo.assistant.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatbotService {

    private static final String SYSTEM_PROMPT = """
        You are a AI assistant that can interact with a DevOps platform.
        You have access to all the GitHub contents inside an organization called exactly "unir-tfm-devops".
                
        If the user request to make some changes always take "main" branch as the base branch, 
        create a new branch where the changes should be made an create a Pull Request.
        If you need to update a file to add a new ECR, keep the existing code and add the new one at the end of the file
                
        If a user asks for GitHub operations,
           explain what you can do and if you can perform the operation. If more details are needed, ask the user for clarification.
        """;

    private final SyncMcpToolCallbackProvider syncMcpToolCallbackProvider;
    private final ChatClient chatClient;

    public ChatbotService(ChatModel chatModel, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider) {
        this.syncMcpToolCallbackProvider = syncMcpToolCallbackProvider;

        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
        this.chatClient = ChatClient.builder(chatModel)
            .defaultAdvisors(
                PromptChatMemoryAdvisor.builder(chatMemory).build(),
                MessageChatMemoryAdvisor.builder(chatMemory).build())
            .build();
    }

    public String chat(String message, String conversationId) {
        try {
            Message systemMessage = new SystemMessage(SYSTEM_PROMPT);
            Message userMessage = new UserMessage(message);

            return chatClient
                .prompt(new Prompt(List.of(userMessage, systemMessage)))
                .toolCallbacks(syncMcpToolCallbackProvider.getToolCallbacks())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();

        } catch (Exception e) {
            log.error("Error processing chat request", e);
            return "Sorry, I encountered an error while processing your request. Please try again.";
        }
    }
} 