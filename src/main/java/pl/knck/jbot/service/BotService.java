package pl.knck.jbot.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.knck.jbot.config.AliceBotConfig;
import pl.knck.jbot.dto.ChatRequest;
import pl.knck.jbot.dto.ChatResponse;
import pl.knck.jbot.model.Conversation;
import pl.knck.jbot.model.Session;
import pl.knck.jbot.repository.ConversationRepository;
import pl.knck.jbot.repository.SessionRepository;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotService {

    private final AliceBotConfig aliceBotConfig;
    private final SessionRepository sessionRepository;
    private final ConversationRepository conversationRepository;
    private final Map<String, Bot> bots = new ConcurrentHashMap<>();
    private final Map<String, Chat> chatSessions = new ConcurrentHashMap<>();

    private Bot getOrLoadBot(String botName) {
        return bots.computeIfAbsent(botName, name -> {
            try {
                log.info("Loading bot {}", name);
                return aliceBotConfig.bots(aliceBotConfig.resourcesPath()).get(name);
            } catch (IOException e) {
                log.error("Error loading bot {}", name, e);
                throw new RuntimeException("Error loading bot: " + name, e);
            }
        });
    }

    private String createNewSession(String botName, String username) {
        log.info("Creating new session for bot: {}", botName);
        Session session = new Session();
        session.setBotName(botName);
        session.setUsername(username);
        sessionRepository.save(session);
        String sessionId = session.getUuid();
        Bot bot = getOrLoadBot(botName);
        chatSessions.put(sessionId, new Chat(bot));
        log.info("Created new chat session: {} for bot: {}", sessionId, botName);
        return sessionId;
    }

    public ChatResponse chat(String message, String sessionId, String botName, String username) {
        String activeSessionId = (sessionId != null && chatSessions.containsKey(sessionId))
                ? sessionId
                : createNewSession(botName, username);
        String activeMessage = (message.isBlank()) ? message : "Hello";
        Chat chat = chatSessions.get(activeSessionId);
        String response = chat.multisentenceRespond(message);
        Conversation conversation = new Conversation();
        conversation.setMessageRequest(activeMessage);
        conversation.setMessageResponse(response);
        conversation.setSessionId(activeSessionId);
        conversation.setTopic(chat.predicates.get("topic"));
        conversationRepository.save(conversation);
        return new ChatResponse(response, botName, activeSessionId, username);
    }


    public void closeSession(String sessionId) {
        chatSessions.remove(sessionId);
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setStatus("CLOSED");
            sessionRepository.save(session);
        });
        log.info("Closed session: {}", sessionId);
    }
    public String setBotActive(String botName) {
        return "Bot " + botName + " is now active.";
    }
}

