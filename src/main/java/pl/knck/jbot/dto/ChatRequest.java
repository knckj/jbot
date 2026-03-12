package pl.knck.jbot.dto;

import jakarta.annotation.Nullable;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String message;
    private String botName;
    private String username;
    private String sessionId;
}
