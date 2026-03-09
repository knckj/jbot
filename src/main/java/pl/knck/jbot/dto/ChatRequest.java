package pl.knck.jbot.dto;

import jakarta.annotation.Nullable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String message;
    private String botName;
    private String username;

    @Nullable
    private String sessionId;
}
