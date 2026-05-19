package ru.alarkhipov.crm.records;

import java.time.LocalDateTime;

public record BestPeriodMessage(
        LocalDateTime startDate,
        LocalDateTime endDate,
        int transactionCount
){
}
