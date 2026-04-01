package dev.artiz.financeassistantapi.repository;

import dev.artiz.financeassistantapi.transactions.model.Transaction;
import dev.artiz.financeassistantapi.transactions.model.TransactionCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository
    extends JpaRepository<Transaction, Long>
{
    List<Transaction> findByCategoryOrderByCreatedAtAsc(
        TransactionCategory category
    );
}
