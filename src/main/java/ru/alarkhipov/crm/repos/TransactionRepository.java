package ru.alarkhipov.crm.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alarkhipov.crm.entities.SellerEntity;
import ru.alarkhipov.crm.entities.TransactionEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findBySellerId(Long sellerId);
    List<TransactionEntity> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
}
