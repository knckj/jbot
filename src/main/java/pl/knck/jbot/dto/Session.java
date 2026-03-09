package pl.knck.jbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    private String sessionId;
    private String botName;
    private String status;
    private String username;

}
