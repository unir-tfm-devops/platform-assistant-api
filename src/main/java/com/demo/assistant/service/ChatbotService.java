package com.demo.assistant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final ChatClient chatClient;
    
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

    public String chat(String userMessage) {
        try {
            // Create system message with GitHub MCP capabilities
            SystemMessage systemMessage = new SystemMessage(SYSTEM_PROMPT);
            UserMessage userMsg = new UserMessage(userMessage);
            
            // Create prompt with system and user messages
            Prompt prompt = new Prompt(List.of(systemMessage, userMsg));
            
            // Get response from OpenAI
            ChatResponse response = chatClient.call(prompt);
            
            return response.getResult().getOutput().getContent();
            
        } catch (Exception e) {
            log.error("Error in chatbot service", e);
            return "I apologize, but I encountered an error while processing your request. Please try again.";
        }
    }

    public String chatWithContext(String userMessage, Map<String, Object> context) {
        try {
            // Create a more detailed system prompt with context
            String contextualSystemPrompt = SYSTEM_PROMPT + "\n\n" +
                "Additional context: " + context.toString();
            
            SystemMessage systemMessage = new SystemMessage(contextualSystemPrompt);
            UserMessage userMsg = new UserMessage(userMessage);
            
            Prompt prompt = new Prompt(List.of(systemMessage, userMsg));
            ChatResponse response = chatClient.call(prompt);
            
            return response.getResult().getOutput().getContent();
            
        } catch (Exception e) {
            log.error("Error in chatbot service with context", e);
            return "I apologize, but I encountered an error while processing your request. Please try again.";
        }
    }
} 