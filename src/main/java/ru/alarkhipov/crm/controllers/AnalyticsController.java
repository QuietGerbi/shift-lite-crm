package ru.alarkhipov.crm.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alarkhipov.crm.records.AnalyticsMessage;
import ru.alarkhipov.crm.records.BestPeriodMessage;
import ru.alarkhipov.crm.services.AnalyticsService;

import java.time.LocalDateTime;
import java.util.List;

// эндпоинты для раздела аналитики
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // под option имеется ввиду один из вариантов: day, month, quarter, year
    @GetMapping("/most-productive/{option}")
    public ResponseEntity<AnalyticsMessage> getMostProductiveSellerByPeriod(
            @PathVariable("option") String option
    ){
        return ResponseEntity.ok(analyticsService.getMostProductiveSellerByPeriod(option));
    }

    // запрос указывается в формате /analytics/less-than?from=yyyy-mm-ddThh:mm:ss&to=yyyy-mm-ddThh:mm:ss&amount=AMOUNT_NUM
    @GetMapping("/less-than")
    public ResponseEntity<List<AnalyticsMessage>> getSellersWithAmountLessThan(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam Long amount
    ){
        return ResponseEntity.ok(analyticsService.getSellersWithAmountLessThan(from, to, amount));
    }

    @GetMapping("/best-period/{sellerId}")
    public ResponseEntity<BestPeriodMessage> getBestPeriodForSeller(
            @PathVariable("sellerId") Long sellerId
    ){
        return ResponseEntity.ok(analyticsService.getBestPeriodForSeller(sellerId));
    }
}
