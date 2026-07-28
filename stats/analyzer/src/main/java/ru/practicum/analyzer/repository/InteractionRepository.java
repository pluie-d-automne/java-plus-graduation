package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.model.Interaction;

public interface InteractionRepository extends JpaRepository<Interaction, Long> {
}
