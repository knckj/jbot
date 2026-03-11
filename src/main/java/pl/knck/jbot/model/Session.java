package pl.knck.jbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;


@Data
@NoArgsConstructor
@Table(name = "sessions")
@Entity
public class Session {

    @Id
    @UuidGenerator
    private String uuid;
    private String botName;
    private String status;
    private String username;

    @UpdateTimestamp
    private Timestamp lastMessage;

}
