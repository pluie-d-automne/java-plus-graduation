package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.analyzer.dto.SimilarEvent;
import ru.practicum.analyzer.model.Interaction;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface InteractionRepository extends JpaRepository<Interaction, Long> {

    Optional<Interaction> findByUserIdAndEventId(Long userId, Long eventId);

    // Похожие мероприятия
    @Query("""
            SELECT i.eventId
            FROM Interaction AS i
            WHERE i.userId= :userId
            """)
    Set<Long> findEventIdByUserId(Long userId);

    // Сумма максимальных весов взаимодействия
    @Query("""
            SELECT new ru.practicum.analyzer.dto.SimilarEvent(i.eventId, SUM(i.rating))
            FROM Interaction AS i
            WHERE i.eventId IN :eventIds
            GROUP BY i.eventId
            """)
    List<SimilarEvent> getInteractionsCount(List<Long> eventIds);

    // N мероприятий, с которыми последнее время взаимодействовл пользователь
    @Query("""
            SELECT i.eventId
            FROM Interaction AS i
            WHERE i.userId = :userId
            ORDER BY i.timestamp DESC
            LIMIT :n
            """)
    List<Long> findRecentEventsByUserId(Long userId, Integer n);

    // Оценки пользователя, проставленные мероприятиям из списка
    @Query("""
            SELECT new ru.practicum.analyzer.dto.SimilarEvent(i.eventId, i.rating)
            FROM Interaction AS i
            WHERE i.userId= :userId AND i.eventId IN :eventIds
            """)
    List<SimilarEvent> findEventsByUserIdAndEvents(Long userId, List<Long> eventIds);
}
