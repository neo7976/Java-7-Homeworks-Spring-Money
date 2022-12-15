package sobinda.moneybysobin.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sobinda.moneybysobin.exceptions.InvalidTransactionExceptions;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(InvalidTransactionExceptions.class)
    public ResponseEntity<String> invalidTransaction(InvalidTransactionExceptions e) {
        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .body(e.getMessage());
    }
}
