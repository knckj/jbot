package pl.knck.jbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.knck.jbot.config.AliceBotConfig;
import pl.knck.jbot.dto.ActiveSessionDTO;
import pl.knck.jbot.dto.ChatRequest;
import pl.knck.jbot.dto.ChatResponse;
import pl.knck.jbot.exceptions.BotNotFoundException;
import pl.knck.jbot.model.Conversation;
import pl.knck.jbot.model.Session;
import pl.knck.jbot.repository.ConversationRepository;
import pl.knck.jbot.repository.SessionRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotService {

    private final AliceBotConfig aliceBotConfig;
    private final SessionRepository sessionRepository;
    private final ConversationRepository conversationRepository;
    private final Map<String, Bot> bots = new ConcurrentHashMap<>();
    private final Map<String, Chat> chatSessions = new ConcurrentHashMap<>();
    private final Map<String, Instant> sessionLastActive = new ConcurrentHashMap<>();

    @Value("${jbot.session.timeout:300000}")
    private long sessionTimeoutMS;

    private Bot getOrLoadBot(String botName) {
        return bots.computeIfAbsent(botName, name -> {
            try {
                log.info("Loading bot {}", name);
                if (doesBotExist(name)) {
                    return aliceBotConfig
                            .bots(aliceBotConfig
                                    .resourcesPath())
                            .get(name);
                } else  {
                    throw new BotNotFoundException("Bot " + name + " not found");
                }
            } catch (IOException e) {
                    throw new BotNotFoundException("Bot " + name + " not found");}
                }
            );
    }

    private Boolean doesBotExist(String botName) {
        try {
            return aliceBotConfig.bots(aliceBotConfig.resourcesPath()).containsKey(botName);
        } catch (IOException e) {
            return false;
        }
    }

    private String createNewSession(String botName, String username) {
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

    private String activeSession(ChatRequest chatRequest) {
        return (chatRequest.getSessionId().isBlank()
                        && chatSessions.containsKey(
                        chatRequest.getSessionId()))
                ? chatRequest.getSessionId()
                : createNewSession(
                chatRequest.getBotName(),
                chatRequest.getUsername()
        );
    }

    private String getActiveMessage(ChatRequest chatRequest) {
        return (
                chatRequest.getMessage().isBlank()
                ? "Hello"
                : chatRequest.getMessage()
                );
    }

    private void addConversationEntry(String sessionId, String message, String response, String topic) {
        Conversation conversation = Conversation.builder()
                .sessionId(sessionId)
                .messageRequest(message)
                .messageResponse(response)
                .topic(topic)
                .build();
        conversationRepository.save(conversation);
    }

    public ChatResponse chat(ChatRequest chatRequest) {
        String activeSessionId = activeSession(chatRequest);
        String activeMessage = getActiveMessage(chatRequest);
        Chat chat = chatSessions.get(activeSessionId);
        String response = chat.multisentenceRespond(activeMessage);
        String topic = chat.predicates.get("topic");
        addConversationEntry(activeSessionId, activeMessage, response, topic);
        sessionLastActive.put(activeSessionId, Instant.now());

        return ChatResponse.builder()
                .response(response)
                .username(chatRequest.getUsername())
                .botName(chatRequest.getBotName())
                .sessionId(activeSessionId)
                .build();
    }

    public void closeSession(String sessionId) {
        chatSessions.remove(sessionId);
        sessionLastActive.remove(sessionId);
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setStatus("CLOSED");
            sessionRepository.save(session);
        });
        log.info("Closed session: {}", sessionId);
    }

    @Scheduled(fixedDelayString = "${jbot.session.cleanup.interval:60000}")
    public void cleanupStaleSessions() {
        log.info("Cleaning up stale sessions");
        Instant cutoff = Instant
                .now().minusMillis(sessionTimeoutMS);
        sessionLastActive.entrySet().stream()
                .filter(entry -> entry.getValue().isBefore(cutoff))
                .map(Map.Entry::getKey)
                .forEach(this::closeSession);
    }

    public ArrayList<ActiveSessionDTO> getActiveSessions() {
        return chatSessions
                .keySet()
                .stream()
                .map(s -> new ActiveSessionDTO(s, chatSessions.get(s).bot.name)
                ).collect(Collectors.toCollection(ArrayList::new));

    }

}

