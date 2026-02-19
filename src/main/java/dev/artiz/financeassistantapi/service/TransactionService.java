package dev.artiz.financeassistantapi.service;

import dev.artiz.financeassistantapi.dto.TransactionDTO;
import dev.artiz.financeassistantapi.model.Transaction;
import dev.artiz.financeassistantapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @CacheEvict(value = "transactions", allEntries = true)
    public TransactionDTO.Get create(TransactionDTO.Create request) {
        Transaction transaction = Transaction.builder()
                .description(request.description())
                .amount(request.amount())
                .category(request.category())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        return mapToResponse(saved);
    }

    @Cacheable(value = "transactions")
    public List<TransactionDTO.Get> getTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @CacheEvict(value = "transactions", allEntries = true)
    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found with id: " + id);
        }

        transactionRepository.deleteById(id);
    }

    private TransactionDTO.Get mapToResponse(Transaction t) {
        return new TransactionDTO.Get(
                t.getId(),
                t.getDescription(),
                t.getAmount(),
                t.getCreatedAt()
        );
    }
}
