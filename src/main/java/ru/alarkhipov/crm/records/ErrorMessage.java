package ru.alarkhipov.crm.records;

import java.time.LocalDateTime;

public record ErrorMessage(
        String messageForUser,
        String detailedMessage,
        LocalDateTime errorTime
){
}
