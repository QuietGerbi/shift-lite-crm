package ru.alarkhipov.crm.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.alarkhipov.crm.entities.SellerEntity;
import ru.alarkhipov.crm.entities.TransactionEntity;
import ru.alarkhipov.crm.records.AnalyticsMessage;
import ru.alarkhipov.crm.records.BestPeriodMessage;
import ru.alarkhipov.crm.repos.SellerRepository;
import ru.alarkhipov.crm.repos.TransactionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class AnalyticsService {
    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;

    public AnalyticsService(TransactionRepository transactionRepository, SellerRepository sellerRepository) {
        this.transactionRepository = transactionRepository;
        this.sellerRepository = sellerRepository;
    }

    public AnalyticsMessage getMostProductiveSellerByPeriod(String option){
        LocalDateTime startDate = defineDate(option);
        List<TransactionEntity> transactions = transactionRepository.findByTransactionDateBetween(startDate, LocalDateTime.now());

        if (transactions.isEmpty()){
            log.info("Transactions list is empty");
            return null;
        }

        HashMap<Long, Long> transactionsMap = new HashMap<>();
        transactions.forEach(
                transaction -> transactionsMap.merge(
                        transaction.getSeller().getId(),
                        transaction.getAmount(), Long::sum)
        );

        Map.Entry<Long, Long> topSellerSet = transactionsMap
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (topSellerSet == null){
            return null;
        }

        String topSellerName = sellerRepository.findById(topSellerSet.getKey())
                .map(SellerEntity::getName)
                .orElse("Not found");

        log.info("Best seller found: id {}, Total Sales: {}", topSellerSet.getKey(), topSellerSet.getValue());
        return new AnalyticsMessage(topSellerSet.getKey(), topSellerName, topSellerSet.getValue());
    }

    public List<AnalyticsMessage> getSellersWithAmountLessThan(LocalDateTime from, LocalDateTime to, Long maxAmount){
        List<TransactionEntity> transactions = transactionRepository.findByTransactionDateBetween(from, to);

        if (transactions.isEmpty()) {
            log.info("Transactions list is empty");
            return null;
        }

        Map<Long, Long> salesMap = new HashMap<>();
        Map<Long, String> namesMap = new HashMap<>();

        for (TransactionEntity transaction : transactions) {
            Long sellerId = transaction.getSeller().getId();
            Long amount = transaction.getAmount();

            salesMap.merge(sellerId, amount, Long::sum);
            namesMap.put(sellerId, transaction.getSellerName());
        }

        List<AnalyticsMessage> sellersList = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : salesMap.entrySet()) {
            Long sellerId = entry.getKey();
            Long totalAmount = entry.getValue();

            if (totalAmount < maxAmount) {
                String sellerName = namesMap.get(sellerId);
                sellersList.add(new AnalyticsMessage(sellerId, sellerName, totalAmount));
            }
        }
        log.info("Sending sellers list with amount less than {}", maxAmount);
        return sellersList;
    }

    public BestPeriodMessage getBestPeriodForSeller(Long sellerId) {
        SellerEntity sellerEntity = sellerRepository.findById(sellerId).orElseThrow(() ->
                new EntityNotFoundException("Seller not found by id: " + sellerId)
        );
        List<TransactionEntity> transactions = transactionRepository.findBySellerId(sellerId);

        if (transactions.isEmpty()) {
            log.info("Transactions list is empty");
            return null;
        }
        if (transactions.size() == 1) {
            LocalDateTime date = transactions.getFirst().getTransactionDate();
            log.info("Sending best period for seller with id: {}", sellerId);
            return new BestPeriodMessage(date, date, 1);
        }

        transactions.sort(Comparator.comparing(TransactionEntity::getTransactionDate));

        int bestCount = 0;
        LocalDateTime bestStart = transactions.getFirst().getTransactionDate();
        LocalDateTime bestEnd = transactions.getFirst().getTransactionDate();
        double maxDensity = 0.0;

        for (int i = 0; i < transactions.size(); i++) {
            for (int j = i; j < transactions.size(); j++) {

                LocalDateTime start = transactions.get(i).getTransactionDate();
                LocalDateTime end = transactions.get(j).getTransactionDate();

                int count = j - i + 1;

                long days = getDaysBetween(start, end);
                if (days == 0) days = 1;

                double density = (double) count / days;

                if (density > maxDensity || (density == maxDensity && count > bestCount)) {
                    maxDensity = density;
                    bestCount = count;
                    bestStart = start;
                    bestEnd = end;
                }
            }
        }
        log.info("Sending best period for seller with id: {}", sellerId);
        return new BestPeriodMessage(bestStart, bestEnd, bestCount);
    }

    private long getDaysBetween(LocalDateTime start, LocalDateTime end) {
        return java.time.temporal.ChronoUnit.DAYS.between(start, end);
    }

    private LocalDateTime defineDate(String option){
        LocalDateTime startDate = null;
        switch (option){
            case "day":
                startDate = LocalDate.now().atStartOfDay();
                break;
            case "month":
                startDate = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                break;
            case "quarter":
                LocalDate today = LocalDate.now();
                int currentMonth = today.getMonthValue();
                int firstMonthOfQuarter = ((currentMonth - 1) / 3) * 3 + 1;
                startDate = today.withMonth(firstMonthOfQuarter).withDayOfMonth(1).atStartOfDay();
                break;
            case "year":
                startDate = LocalDate.now().withDayOfYear(1).atStartOfDay();
                break;
            default:
                throw new IllegalArgumentException("No such option");
        }
        return startDate;
    }
}
