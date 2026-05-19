package ru.alarkhipov.crm.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.alarkhipov.crm.entities.SellerEntity;
import ru.alarkhipov.crm.entities.TransactionEntity;
import ru.alarkhipov.crm.records.Transaction;
import ru.alarkhipov.crm.repos.SellerRepository;
import ru.alarkhipov.crm.repos.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionServiceTest {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private SellerEntity currentSeller;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        sellerRepository.deleteAll();

        currentSeller = sellerRepository.save(new SellerEntity(null, "Илья GT", "gt-sales@kakoitomail.ru", LocalDateTime.now()));
    }

    @Test
    void getAllTransactionsSuccess() {
        transactionRepository.save(new TransactionEntity(null, currentSeller, "Илья GT", "gt-sales@kakoitomail.ru", 5000L, "CASH", LocalDateTime.now()));

        List<Transaction> result = transactionService.getAllTransactions();

        assertEquals(1, result.size());
        assertEquals(5000L, result.getFirst().amount());
    }

    @Test
    void getTransactionByIdSuccess() {
        TransactionEntity tx = transactionRepository.save(new TransactionEntity(null, currentSeller,
                "Илья GT", "gt-sales@kakoitomail.ru",
                12000L, "CARD", LocalDateTime.now()));

        Transaction result = transactionService.getTransactionById(tx.getId());

        assertNotNull(result);
        assertEquals(12000L, result.amount());
        assertEquals("Илья GT", result.sellerName());
    }

    @Test
    void getTransactionByIdThrowsException() {
        assertThrows(EntityNotFoundException.class, () -> transactionService.getTransactionById(999L));
    }

    @Test
    void createTransactionSuccessShouldSnapshotSellerData() {
        Transaction requestDto = new Transaction(null, currentSeller.getId(),
                null, null,
                25000L, "CARD", LocalDateTime.now());

        Transaction result = transactionService.createTransaction(requestDto);

        assertNotNull(result.id());
        assertEquals("Илья GT", result.sellerName());
        assertEquals("gt-sales@kakoitomail.ru", result.sellerContact());
        assertEquals(25000L, result.amount());
        assertTrue(transactionRepository.existsById(result.id()));
    }

    @Test
    void createTransactionThrowsExceptionWhenSellerNotFound() {
        Transaction invalidDto = new Transaction(null, 999L,
                null, null,
                1000L, "CASH", LocalDateTime.now());
        assertThrows(EntityNotFoundException.class, () -> transactionService.createTransaction(invalidDto));
    }

    @Test
    void getAllTransactionsBySellerIdSuccess() {
        transactionRepository.save(new TransactionEntity(null, currentSeller,
                "Илья GT", "gt-sales@kakoitomail.ru",
                4000L, "CASH", LocalDateTime.now()));

        List<Transaction> result = transactionService.getAllTransactionsBySellerId(currentSeller.getId());

        assertEquals(1, result.size());
        assertEquals(currentSeller.getId(), result.getFirst().sellerId());
    }

    @Test
    void getAllTransactionsBySellerIdThrowsExceptionWhenSellerNotFound() {
        assertThrows(EntityNotFoundException.class, () -> transactionService.getAllTransactionsBySellerId(999L));
    }
}