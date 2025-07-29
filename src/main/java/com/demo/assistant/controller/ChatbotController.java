package com.demo.assistant.controller;

import com.demo.assistant.model.ChatRequest;
import com.demo.assistant.model.ChatResponse;
import com.demo.assistant.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatbotController {

  private final ChatbotService chatbotService;

  @PostMapping
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

  @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter chatStream(@RequestBody ChatRequest request) {
    SseEmitter emitter = new SseEmitter();
    
    try {
      log.info("Received streaming chat request: {}", request.message());

      Flux<String> responseStream = chatbotService.chatStream(request.message(), request.conversationId());

      responseStream.subscribe(
          chunk -> {
            try {
              emitter.send(SseEmitter.event()
                  .name("message")
                  .data(chunk));
            } catch (Exception e) {
              log.error("Error sending SSE chunk", e);
              emitter.completeWithError(e);
            }
          },
          error -> {
            log.error("Error in streaming response", error);
            try {
              emitter.send(SseEmitter.event()
                  .name("error")
                  .data("An error occurred while processing your request"));
              emitter.complete();
            } catch (Exception e) {
              emitter.completeWithError(e);
            }
          },
          () -> {
            try {
              emitter.send(SseEmitter.event()
                  .name("complete")
                  .data(""));
              emitter.complete();
            } catch (Exception e) {
              emitter.completeWithError(e);
            }
          }
      );

    } catch (Exception e) {
      log.error("Error processing streaming chat request", e);
      try {
        emitter.send(SseEmitter.event()
            .name("error")
            .data("An error occurred while processing your request"));
        emitter.complete();
      } catch (Exception ex) {
        emitter.completeWithError(ex);
      }
    }

    return emitter;
  }
}
