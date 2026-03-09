package pl.knck.jbot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.knck.jbot.dto.ChatRequest;
import pl.knck.jbot.dto.ChatResponse;
import pl.knck.jbot.service.BotService;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class BotController {

    @Autowired
    public BotService botService;

    @GetMapping("/reload/{botName}")
    public ResponseEntity<String> loadBots(@PathVariable String botName) {
        return ResponseEntity.ok("Bot " + botName + " reloaded successfully.");
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest chatRequest) {
        log.info("Received chat request: {}", chatRequest);
        return ResponseEntity.ok(
                botService.getChatResponse(
                chatRequest.getMessage(),
                chatRequest.getBotName(),
                String.valueOf(chatRequest.getSessionId()),
                chatRequest.getUsername()
                )
        );
    }
}