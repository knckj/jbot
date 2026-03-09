package pl.knck.jbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.knck.jbot.model.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
