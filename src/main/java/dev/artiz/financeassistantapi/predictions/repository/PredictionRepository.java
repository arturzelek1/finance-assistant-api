package dev.artiz.financeassistantapi.predictions.repository;

import dev.artiz.financeassistantapi.predictions.model.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {}
