package pl.knck.jbot.service;

import lombok.extern.slf4j.Slf4j;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.knck.jbot.config.AliceBotConfig;
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
public class BotService {

    private final AliceBotConfig aliceBotConfig;
    private final SessionRepository sessionRepository;
    private final ConversationRepository conversationRepository;
    private final Map<String, Bot> bots = new ConcurrentHashMap<>();
    private final Map<String, Chat> chatSessions = new ConcurrentHashMap<>();

    @Autowired
    private String resourcesPath;

    public BotService(AliceBotConfig aliceBotConfig, SessionRepository sessionRepository, ConversationRepository conversationRepository) {
        this.aliceBotConfig = aliceBotConfig;
        this.sessionRepository = sessionRepository;
        this.conversationRepository = conversationRepository;
    }

    private Bot getOrLoadBot(String botName) {
        return bots.computeIfAbsent(botName, name -> {
            try {
                return aliceBotConfig.bots(resourcesPath).get(name);
            } catch (IOException e) {
                throw new RuntimeException("Error loading bot: " + name, e);
            }
        });
    }

    private String createNewSession(String botName, String username) {
        Session session = new Session();
        session.setBotName(botName);
        session.setUsername(username);
        sessionRepository.save(session);

        String sessionId = session.getUuid();
        Bot bot = getOrLoadBot(botName);
        chatSessions.put(sessionId, new Chat(bot));
        log.info("Created new chat session: {}", sessionId);
        return sessionId;
    }

    private void createMessage(String sessionId, String messageRequest, String messageResponse, String topic) {
        Conversation conversation = new Conversation();
        conversation.setSessionId(sessionId);
        conversation.setMessageRequest(messageRequest);
        conversation.setMessageResponse(messageResponse);
        conversation.setTopic(topic);
        conversationRepository.save(conversation);
    }


    public ChatResponse getChatResponse(String chatMessage, String botName, String sessionId, String username) {
        String activeSessionId = (sessionId != null && chatSessions.containsKey(sessionId))
                ? sessionId
                : createNewSession(botName, username);
        Chat chat = chatSessions.get(activeSessionId);
        String response = chat.multisentenceRespond(chatMessage);
        String topic = chat.predicates.get("topic");
        createMessage(activeSessionId, chatMessage, response, topic);
        log.info("Session: {}, response: {}", activeSessionId, response);
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

