package ru.alarkhipov.crm.records;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import ru.alarkhipov.crm.entities.SellerEntity;

import java.time.LocalDateTime;

public record Transaction (
        @Null
        Long id,
        @NotNull
        Long sellerId,
        @Null
        String sellerName,
        @Null
        String sellerContact,
        @NotNull
        Long amount,
        @NotBlank
        @Pattern(regexp = "^(CASH|CARD|TRANSFER)$")
        String paymentType,
        @NotNull
        @PastOrPresent
        LocalDateTime transactionDate
){
}
