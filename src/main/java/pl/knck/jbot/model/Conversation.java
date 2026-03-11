package pl.knck.jbot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;

import java.sql.Timestamp;

@Data
@Table(name = "conversations")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CurrentTimestamp
    private Timestamp timestamp;
    private String sessionId;
    @Column(columnDefinition = "TEXT")
    private String messageRequest;
    @Column(columnDefinition = "TEXT")
    private String messageResponse;
    private String topic;

}