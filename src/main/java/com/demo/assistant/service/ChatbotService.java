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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatbotService {

  @Value("classpath:context/system.txt")
  private Resource systemContext;

  private final SyncMcpToolCallbackProvider syncMcpToolCallbackProvider;
  private final ChatClient chatClient;

  public ChatbotService(
      ChatModel chatModel, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider) {
    this.syncMcpToolCallbackProvider = syncMcpToolCallbackProvider;

    ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
    this.chatClient =
        ChatClient.builder(chatModel)
            .defaultAdvisors(
                PromptChatMemoryAdvisor.builder(chatMemory).build(),
                MessageChatMemoryAdvisor.builder(chatMemory).build())
            .build();
  }

  public String chat(String message, String conversationId) {
    try {
      Message systemMessage = new SystemMessage(systemContext);
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
