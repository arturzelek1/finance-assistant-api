package dev.artiz.financeassistantapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "predictions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prediction implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TransactionCategory category;
    private BigDecimal predictedAmount;
    private LocalDateTime targetDate;
    private Double confidenceLevel;
    private Double modelFit;
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

//    Only for education purpose, because Lombok provides @Builder
//    private Prediction(Builder builder) {
//        this.predictedAmount = builder.predictedAmount;
//        this.category = builder.category;
//        this.targetDate = builder.targetDate;
//        this.createdAt = builder.createdAt;
//        this.confidenceLevel = builder.confidenceLevel;
//        this.modelFit = builder.modelFit;
//    }
//
//    public static class Builder {
//        private BigDecimal predictedAmount;
//        private TransactionCategory category;
//        private LocalDateTime targetDate;
//        private LocalDateTime createdAt;
//        private Double confidenceLevel;
//        private Double modelFit;
//
//        public Builder predictedAmount(BigDecimal predictedAmount) {
//            this.predictedAmount = predictedAmount;
//            return this;
//        }
//
//        public Builder category(TransactionCategory category) {
//            this.category = category;
//            return this;
//        }
//
//        public Builder targetDate(LocalDateTime date) {
//            this.targetDate = date;
//            return this;
//        }
//
//        public Builder createdAt(LocalDateTime createdAt) {
//            this.createdAt = createdAt;
//            return this;
//        }
//
//        public Builder confidenceLevel(Double confidenceLevel) {
//            this.confidenceLevel = confidenceLevel;
//            return this;
//        }
//
//        public Builder modelFit(Double modelFit) {
//            this.modelFit = modelFit;
//            return this;
//        }
//
//        public Prediction build() {
//            return new Prediction(this);
//        }
//    }
}


