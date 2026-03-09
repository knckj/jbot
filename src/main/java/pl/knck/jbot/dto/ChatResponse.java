package pl.knck.jbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatResponse {
    private String response;
    private String botName;
    private String sessionId;
    private String username;
}
