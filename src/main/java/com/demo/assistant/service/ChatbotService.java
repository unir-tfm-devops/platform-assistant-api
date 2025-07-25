package com.demo.assistant.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private static final String SYSTEM_PROMPT = """
        You are a helpful AI assistant that can interact with GitHub through the MCP (Model Context Protocol) server.
        You have access to GitHub repositories, issues, pull requests, actions, and code security features.
                
        When users ask about GitHub-related tasks, you can:
        - Search repositories and code
        - Create and manage issues and pull requests
        - View repository information
        - Check GitHub Actions workflows
        - Analyze code security
                
        Always be helpful and provide clear, actionable responses. If a user asks for GitHub operations,
        explain what you can do and ask for specific details like repository names, issue titles, etc.
                
        For non-GitHub questions, provide general assistance as a helpful AI assistant.
        """;

    private final ChatModel chatModel;

    public String chat(String message) {
        try {
            Message systemMessage = new SystemMessage(SYSTEM_PROMPT);
            Message userMessage = new UserMessage(message);

            return ChatClient.create(chatModel)
                .prompt(new Prompt(List.of(userMessage, systemMessage)))
                .call()
                .content();

        } catch (Exception e) {
            log.error("Error processing chat request", e);
            return "Sorry, I encountered an error while processing your request. Please try again.";
        }
    }
} 