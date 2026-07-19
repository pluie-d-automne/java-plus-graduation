package ewm.core.repository;

import ewm.core.dto.ConfirmedRequestCount;
import ewm.core.model.ParticipationRequest;
import ewm.core.dto.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    // Список своих заявок на участия в событиях
    List<ParticipationRequest> findByRequesterId(Long requesterId);

    // Проверяем наличие такого запроса
    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    // Количество заявок на событие
    Long countByEventIdAndStatus(Long eventId, ParticipationStatus status);

    // Количество заявок для событий
    @Query("""
            SELECT new ewm.core.dto.ConfirmedRequestCount(r.eventId, COUNT(r.id))
            FROM ParticipationRequest AS r
            WHERE r.eventId IN :eventIds AND r.status = 'CONFIRMED'
            GROUP BY r.eventId
            """)
    List<ConfirmedRequestCount> findAllConfirmedRequestsByEventId(List<Long> eventIds);

    List<ParticipationRequest> findByEventId(Long eventId);
}
