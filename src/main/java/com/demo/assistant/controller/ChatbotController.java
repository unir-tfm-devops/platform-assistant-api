package com.demo.assistant.controller;

import com.demo.assistant.model.ChatRequest;
import com.demo.assistant.model.ChatResponse;
import com.demo.assistant.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@Slf4j
public class ChatbotController {

  private final ChatbotService chatbotService;

  @PostMapping("/chat")
  public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
    try {
      log.info("Received chat request: {}", request.message());

      String response = chatbotService.chat(request.message(), request.conversationId());

      ChatResponse chatResponse = new ChatResponse(response, true);

      return ResponseEntity.ok(chatResponse);

    } catch (Exception e) {
      log.error("Error processing chat request", e);

      ChatResponse errorResponse =
          new ChatResponse("An error occurred while processing your request", false);

      return ResponseEntity.internalServerError().body(errorResponse);
    }
  }
}
