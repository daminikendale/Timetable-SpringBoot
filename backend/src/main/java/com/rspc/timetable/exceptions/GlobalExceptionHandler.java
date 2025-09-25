// src/main/java/com/rspc/timetable/exceptions/GlobalExceptionHandler.java
package com.rspc.timetable.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleAll(Throwable ex) {
        ErrorResponse body = new ErrorResponse(
            ex.getClass().getSimpleName(),
            ex.getMessage()
        );
        return ResponseEntity.internalServerError().body(body);
    }
}
