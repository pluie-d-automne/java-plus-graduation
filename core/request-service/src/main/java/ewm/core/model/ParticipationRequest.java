package ewm.core.model;

import ewm.core.dto.ParticipationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requester_id", nullable = false)
    @ToString.Exclude
    private Long requesterId;

    @Column(name = "event_id", nullable = false)
    @ToString.Exclude
    private Long eventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationStatus status;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime created;
}
