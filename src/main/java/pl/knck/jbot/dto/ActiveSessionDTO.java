package pl.knck.jbot.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ActiveSessionDTO {
    private String sessionId;
    private String botName;
}
