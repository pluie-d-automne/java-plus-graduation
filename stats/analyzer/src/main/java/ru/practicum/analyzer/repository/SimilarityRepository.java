package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.analyzer.dto.SimilarEvent;
import ru.practicum.analyzer.model.Similarity;

import java.util.List;
import java.util.Optional;

public interface SimilarityRepository extends JpaRepository<Similarity, Long> {
    Optional<Similarity> findByEvent1AndEvent2(Long event1, Long event2);

    // Похожие мероприятия
    @Query("""
            SELECT new ru.practicum.analyzer.dto.SimilarEvent(
                CASE WHEN s.event1 = :eventId THEN s.event2 ELSE s.event1 END, s.similarity)
            FROM Similarity AS s
            WHERE s.event1 = :eventId OR s.event2 = :eventId
            ORDER BY s.similarity DESC
            """)
    List<SimilarEvent> findAllSimilarEvents(Long eventId);

    // Похожие мероприятия для списка
    @Query("""
            SELECT new ru.practicum.analyzer.dto.SimilarEvent(
                CASE WHEN s.event1 = :eventId THEN s.event2 ELSE s.event1 END, s.similarity)
            FROM Similarity AS s
            WHERE (s.event1 IN :eventIds AND NOT s.event2 IN :eventIds)
                OR (s.event2 IN :eventIds AND NOT s.event1 IN :eventIds)
            ORDER BY s.similarity DESC
            """)
    List<SimilarEvent> findAllSimilarEventsByList(List<Long> eventIds);
}
