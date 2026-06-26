package dev.artiz.financeassistantapi.transactions;

import dev.artiz.financeassistantapi.transactions.dto.TransactionDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionsController {

    private final TransactionService transactionService;

    @PostMapping
    public Mono<ResponseEntity<TransactionDTO.Get>> createTransaction(
        @Valid @RequestBody TransactionDTO.Create request,
        @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();

        return transactionService
            .create(request, userId)
            .map(saved -> ResponseEntity.status(201).body(saved));
    }

    @GetMapping
    public Flux<TransactionDTO.Get> getTransactions() {
        return transactionService.getTransactions();
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTransaction(@PathVariable Long id) {
        return transactionService
            .delete(id)
            .thenReturn(ResponseEntity.noContent().build());
    }
}
