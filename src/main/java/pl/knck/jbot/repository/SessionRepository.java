package pl.knck.jbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import pl.knck.jbot.model.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {

}
