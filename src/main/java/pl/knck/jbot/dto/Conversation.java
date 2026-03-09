package pl.knck.jbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Conversation {
    private String sessionId;
    private String messageRequest;
    private String messageResponse;
    private String topic;
}
