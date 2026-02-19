package dev.artiz.financeassistantapi.repository;

import dev.artiz.financeassistantapi.model.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> { }
