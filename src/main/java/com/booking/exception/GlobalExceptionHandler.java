package com.booking.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import feign.FeignException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiError> build(HttpStatus status, String message, WebRequest req) {
        ApiError error = new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, WebRequest req) {
        String msg = ex.getBindingResult().getFieldError().getDefaultMessage();
        return build(HttpStatus.BAD_REQUEST, msg, req);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiError> handleFeign(FeignException ex, WebRequest req) {
        return build(HttpStatus.SERVICE_UNAVAILABLE,
                "Flight service is unreachable: " + ex.getMessage(), req);
    }

    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntime(RuntimeException ex, WebRequest req) {

        if (ex.getMessage().contains("Booking not found")) {
            return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
        }

        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, WebRequest req) {
        ex.printStackTrace();
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req);
    }
}
