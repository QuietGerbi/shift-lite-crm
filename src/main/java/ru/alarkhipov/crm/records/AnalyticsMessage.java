package ru.alarkhipov.crm.records;

public record AnalyticsMessage(
        Long sellerId,
        String sellerName,
        Long totalAmount
){}
