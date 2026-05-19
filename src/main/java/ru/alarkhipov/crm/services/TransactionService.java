package ru.alarkhipov.crm.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.alarkhipov.crm.entities.SellerEntity;
import ru.alarkhipov.crm.entities.TransactionEntity;
import ru.alarkhipov.crm.records.Seller;
import ru.alarkhipov.crm.records.Transaction;
import ru.alarkhipov.crm.repos.SellerRepository;
import ru.alarkhipov.crm.repos.TransactionRepository;

import java.util.List;

@Slf4j
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              SellerRepository sellerRepository) {
        this.transactionRepository = transactionRepository;
        this.sellerRepository = sellerRepository;
    }

    public List<Transaction> getAllTransactions() {
        List<TransactionEntity> allTransactions = transactionRepository.findAll();
        log.info("List of all transactions was sent");
        return allTransactions.stream().map(this::getTransactionByEntity).toList();
    }

    public Transaction getTransactionById(Long id) {
        TransactionEntity transactionEntity = transactionRepository.findById(id).orElseThrow(() -> {
            log.error("Transaction not found by id {}", id);
            return new EntityNotFoundException("Transaction not found by id " + id);
        });
        return getTransactionByEntity(transactionEntity);
    }

    public Transaction createTransaction(Transaction transactionToCreate){
        SellerEntity sellerEntity = sellerRepository.findById(
                transactionToCreate.sellerId())
                .orElseThrow(() -> {
                    log.error("Seller not found by id {}", transactionToCreate.sellerId());
                    return new EntityNotFoundException("Seller not found by id: " +
                            transactionToCreate.sellerId());
                });
        TransactionEntity transactionToSave = new TransactionEntity(
                null,
                sellerEntity,
                sellerEntity.getName(),
                sellerEntity.getContactInfo(),
                transactionToCreate.amount(),
                transactionToCreate.paymentType(),
                transactionToCreate.transactionDate()

        );
        TransactionEntity savedTransaction = transactionRepository.save(transactionToSave);
        log.info("Transaction successfully saved with id {}", savedTransaction.getId());
        return getTransactionByEntity(savedTransaction);
    }

    public List<Transaction> getAllTransactionsBySellerId(Long sellerId){
        if (!sellerRepository.existsById(sellerId)) {
            log.error("Seller not found by id {}", sellerId);
            throw new EntityNotFoundException("Seller not found by id: " + sellerId);
        }
        List<TransactionEntity> allTransactions = transactionRepository.findBySellerId(sellerId);
        log.info("Transaction list for seller id {} was sent", sellerId);
        return allTransactions.stream().map(this::getTransactionByEntity).toList();
    }

    private Transaction getTransactionByEntity(TransactionEntity transaction){
        return new Transaction(
                transaction.getId(),
                transaction.getSeller().getId(),
                transaction.getSellerName(),
                transaction.getSellerContact(),
                transaction.getAmount(),
                transaction.getPaymentType(),
                transaction.getTransactionDate()
        );
    }
}
