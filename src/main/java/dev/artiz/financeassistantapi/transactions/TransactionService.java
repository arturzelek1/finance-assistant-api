package dev.artiz.financeassistantapi.transactions;

import dev.artiz.financeassistantapi.transactions.dto.TransactionDTO;
import dev.artiz.financeassistantapi.transactions.model.Transaction;
import dev.artiz.financeassistantapi.transactions.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @CacheEvict(value = "transactions", allEntries = true)
    public Mono<TransactionDTO.Get> create(
        TransactionDTO.Create request,
        String userId
    ) {
        return Mono
            .fromCallable(() -> {
                Transaction transaction = Transaction.builder()
                    .description(request.description())
                    .amount(request.amount())
                    .category(request.category())
                    .userId(userId)
                    .build();

                Transaction saved = transactionRepository.save(transaction);

                return mapToResponse(saved);
            })
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Cacheable(value = "transactions")
    public Flux<TransactionDTO.Get> getTransactions() {
        return Mono
            .fromCallable(() ->
                transactionRepository
                    .findAll()
                    .stream()
                    .map(this::mapToResponse)
                    .toList()
            )
            .subscribeOn(Schedulers.boundedElastic())
            .flatMapMany(Flux::fromIterable);
    }

    @CacheEvict(value = "transactions", allEntries = true)
    public Mono<Void> delete(Long id) {
        return Mono
            .fromRunnable(() -> {
                if (!transactionRepository.existsById(id)) {
                    throw new RuntimeException(
                        "Transaction not found with id: " + id
                    );
                }

                transactionRepository.deleteById(id);
            })
            .subscribeOn(Schedulers.boundedElastic())
            .then();
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
