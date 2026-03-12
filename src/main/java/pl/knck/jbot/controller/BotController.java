package pl.knck.jbot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.knck.jbot.dto.ActiveSessionDTO;
import pl.knck.jbot.dto.ChatRequest;
import pl.knck.jbot.dto.ChatResponse;
import pl.knck.jbot.exceptions.BotNotFoundException;
import pl.knck.jbot.service.BotService;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BotController {

    private final BotService botService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest chatRequest) {
    try {
        return ResponseEntity.ok(
                botService.chat(
                        ChatRequest.builder()
                                .message(chatRequest.getMessage())
                                .botName(chatRequest.getBotName())
                                .username(chatRequest.getUsername())
                                .sessionId(chatRequest.getSessionId())
                                .build()
                )

        );
    } catch (BotNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/sessions")
    public ResponseEntity<ArrayList<ActiveSessionDTO>> getActiveSessions() {
        return ResponseEntity.ok(botService.getActiveSessions());
    }
}