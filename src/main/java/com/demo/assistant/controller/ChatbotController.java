package com.demo.assistant.controller;

import com.demo.assistant.dto.ChatRequest;
import com.demo.assistant.dto.ChatResponse;
import com.demo.assistant.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            log.info("Received chat request: {}", request.getMessage());
            
            String response = chatbotService.chat(request.getMessage());
            
            ChatResponse chatResponse = ChatResponse.builder()
                .message(response)
                .success(true)
                .build();
            
            return ResponseEntity.ok(chatResponse);
            
        } catch (Exception e) {
            log.error("Error processing chat request", e);
            
            ChatResponse errorResponse = ChatResponse.builder()
                .message("An error occurred while processing your request.")
                .success(false)
                .build();
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/chat/context")
    public ResponseEntity<ChatResponse> chatWithContext(
            @RequestBody ChatRequest request,
            @RequestParam Map<String, Object> context) {
        try {
            log.info("Received chat request with context: {}", request.getMessage());
            
            String response = chatbotService.chatWithContext(request.getMessage(), context);
            
            ChatResponse chatResponse = ChatResponse.builder()
                .message(response)
                .success(true)
                .build();
            
            return ResponseEntity.ok(chatResponse);
            
        } catch (Exception e) {
            log.error("Error processing chat request with context", e);
            
            ChatResponse errorResponse = ChatResponse.builder()
                .message("An error occurred while processing your request.")
                .success(false)
                .build();
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "healthy", "service", "chatbot"));
    }
} 