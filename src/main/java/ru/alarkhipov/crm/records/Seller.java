package ru.alarkhipov.crm.records;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record Seller (
        @Null
        Long id,
        @NotBlank
        String name,
        @NotBlank
        String contactInfo,
        @NotNull
        @PastOrPresent
        LocalDateTime registrationDate
){
}
