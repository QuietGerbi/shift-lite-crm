package ru.alarkhipov.crm.handlers;

import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.alarkhipov.crm.records.ErrorMessage;

import java.time.LocalDateTime;

@ControllerAdvice
public class ErrorHandler {

    private static final Logger log = LogManager.getLogger(ErrorHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception e){
        log.info("Caught an Exception: " + e);

        ErrorMessage errorMessage = new ErrorMessage(
                "Internal server error",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }

    @ExceptionHandler(exception = {
            EntityNotFoundException.class,
    })
    public ResponseEntity<ErrorMessage> handleEntityNotFoundException(EntityNotFoundException e){
        log.info("Caught an EntityNotFoundException: " + e);

        ErrorMessage errorMessage = new ErrorMessage(
                "Entity was not found",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(exception = {
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(Exception e){
        log.info("Caught an IllegalArgumentException: " + e);

        ErrorMessage errorMessage = new ErrorMessage(
                "Bad request",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
