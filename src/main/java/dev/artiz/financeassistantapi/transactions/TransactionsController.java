package dev.artiz.financeassistantapi.transactions;

import dev.artiz.financeassistantapi.transactions.dto.TransactionDTO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionsController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionDTO.Get> createTransaction(
        @Valid @RequestBody TransactionDTO.Create request,
        @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();

        return ResponseEntity.status(201).body(
            transactionService.create(request, userId)
        );
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO.Get>> getTransactions() {
        return ResponseEntity.ok(transactionService.getTransactions());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TransactionDTO.Get> deleteTransaction(
        @PathVariable Long id
    ) {
        transactionService.delete(id);
        return ResponseEntity.status(204).build();
    }
}
