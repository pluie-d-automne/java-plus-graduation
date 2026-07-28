package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.model.Similarity;

public interface SimilarityRepository extends JpaRepository<Similarity, Long> {
}
