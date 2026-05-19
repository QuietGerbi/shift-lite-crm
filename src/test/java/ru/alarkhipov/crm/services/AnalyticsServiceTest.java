package ru.alarkhipov.crm.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.alarkhipov.crm.entities.SellerEntity;
import ru.alarkhipov.crm.entities.TransactionEntity;
import ru.alarkhipov.crm.records.AnalyticsMessage;
import ru.alarkhipov.crm.records.BestPeriodMessage;
import ru.alarkhipov.crm.repos.SellerRepository;
import ru.alarkhipov.crm.repos.TransactionRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AnalyticsServiceTest {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private SellerEntity sellerIvan;
    private SellerEntity sellerPetr;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        sellerRepository.deleteAll();

        sellerIvan = sellerRepository.save(new SellerEntity(null, "Илья Иванов",
                "ivanov@kakoitomail.ru", LocalDateTime.now()));
        sellerPetr = sellerRepository.save(new SellerEntity(null, "Петр Петров",
                "petrov@kakoitomail.ru", LocalDateTime.now()));
    }

    @Test
    void getMostProductiveSellerByPeriodSuccess() {
        LocalDateTime now = LocalDateTime.now();
        transactionRepository.save(new TransactionEntity(null, sellerIvan,
                "Илья Иванов", "ivanov@kakoitomail.ru",
                10000L, "CASH", now));
        transactionRepository.save(new TransactionEntity(null, sellerIvan,
                "Илья Иванов", "ivanov@kakoitomail.ru",
                20000L, "CARD", now));
        transactionRepository.save(new TransactionEntity(null, sellerPetr,
                "Петр Петров", "petrov@kakoitomail.ru",
                15000L, "CARD", now));

        AnalyticsMessage result = analyticsService.getMostProductiveSellerByPeriod("month");

        assertNotNull(result);
        assertEquals(sellerIvan.getId(), result.sellerId());
        assertEquals("Илья Иванов", result.sellerName());
        assertEquals(30000L, result.totalAmount());
    }

    @Test
    void getMostProductiveSellerByPeriodEmpty() {
        AnalyticsMessage result = analyticsService.getMostProductiveSellerByPeriod("day");
        assertNull(result);
    }

    @Test
    void getSellersWithAmountLessThanSuccess() {
        LocalDateTime now = LocalDateTime.now();
        transactionRepository.save(new TransactionEntity(null, sellerIvan,
                "Илья Иванов", "ivanov@kakoitomail.ru",
                10000L, "CASH", now));
        transactionRepository.save(new TransactionEntity(null, sellerPetr,
                "Петр Петров", "petrov@kakoitomail.ru",
                80000L, "CARD", now));

        List<AnalyticsMessage> result = analyticsService.getSellersWithAmountLessThan(now.minusDays(1), now.plusDays(1), 50000L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Илья Иванов", result.getFirst().sellerName());
        assertEquals(10000L, result.getFirst().totalAmount());
    }

    @Test
    void getSellersWithAmountLessThanEmpty() {
        LocalDateTime now = LocalDateTime.now();
        List<AnalyticsMessage> result = analyticsService.getSellersWithAmountLessThan(now.minusDays(5), now.minusDays(4), 50000L);
        assertNull(result);
    }

    @Test
    void getBestPeriodForSellerSingleTransaction() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        transactionRepository.save(new TransactionEntity(null, sellerIvan,
                "Илья Иванов", "ivanov@kakoitomail.ru",
                5000L, "CASH", now));

        BestPeriodMessage result = analyticsService.getBestPeriodForSeller(sellerIvan.getId());

        assertNotNull(result);
        assertEquals(1, result.transactionCount());
        assertEquals(now, result.startDate());
        assertEquals(now, result.endDate());
    }

    @Test
    void getBestPeriodForSellerComplexCalculation() {
        LocalDateTime startPoint = LocalDateTime.of(2026, 5, 1, 10, 0).truncatedTo(java.time.temporal.ChronoUnit.MICROS);
        transactionRepository.save(new TransactionEntity(null, sellerIvan,
                "Илья Иванов", "ivanov@kakoitomail.ru",
                1000L, "CASH", startPoint));
        transactionRepository.save(new TransactionEntity(null, sellerIvan,
                "Илья Иванов", "ivanov@kakoitomail.ru",
                2000L, "CARD", startPoint.plusHours(1)));
        transactionRepository.save(new TransactionEntity(null, sellerIvan,
                "Илья Иванов", "ivanov@kakoitomail.ru",
                3000L, "CARD", startPoint.plusHours(2)));

        BestPeriodMessage result = analyticsService.getBestPeriodForSeller(sellerIvan.getId());
        assertNotNull(result);
        assertEquals(3, result.transactionCount());
        assertEquals(startPoint, result.startDate());
    }

    @Test
    void getBestPeriodForSellerEmptyTransactions() {
        BestPeriodMessage result = analyticsService.getBestPeriodForSeller(sellerPetr.getId());
        assertNull(result);
    }

    @Test
    void getBestPeriodForSellerSellerNotFound() {
        assertThrows(EntityNotFoundException.class, () -> analyticsService.getBestPeriodForSeller(999L));
    }
}