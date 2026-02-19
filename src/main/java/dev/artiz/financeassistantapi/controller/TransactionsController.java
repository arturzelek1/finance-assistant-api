package dev.artiz.financeassistantapi.controller;

import dev.artiz.financeassistantapi.dto.TransactionDTO;
import dev.artiz.financeassistantapi.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionsController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionDTO.Get> createTransaction(@Valid @RequestBody TransactionDTO.Create request) {
        return ResponseEntity.status(201).body(transactionService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO.Get>> getTransactions(){
        return ResponseEntity.ok(transactionService.getTransactions());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TransactionDTO.Get> deleteTransaction(@PathVariable Long id){
        transactionService.delete(id);
        return ResponseEntity.status(204).build();
    }
}
