package com.booking.test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import com.booking.exception.ApiError;
import com.booking.exception.GlobalExceptionHandler;

import feign.FeignException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/test-path");
    }

    @Test
    void testHandleValidationError() {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult("req", "req");
        bindingResult.addError(new FieldError("req", "email", "Invalid email"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiError> resp = handler.handleValidation(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals(400, resp.getBody().getStatus());
        assertEquals("Invalid email", resp.getBody().getMessage());
        assertEquals("/test-path", resp.getBody().getPath());
    }

    @Test
    void testHandleFeignException() {
        FeignException ex = mock(FeignException.class);
        when(ex.getMessage()).thenReturn("Service down");

        ResponseEntity<ApiError> resp = handler.handleFeign(ex, webRequest);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, resp.getStatusCode());
        assertTrue(resp.getBody().getMessage().contains("Flight service is unreachable"));
        assertTrue(resp.getBody().getMessage().contains("Service down"));
        assertEquals("/test-path", resp.getBody().getPath());
    }

    @Test
    void testHandleRuntime_BookingNotFound() {
        RuntimeException ex = new RuntimeException("Booking not found");

        ResponseEntity<ApiError> resp = handler.handleRuntime(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertEquals(404, resp.getBody().getStatus());
        assertEquals("Booking not found", resp.getBody().getMessage());
    }

    @Test
    void testHandleRuntime_OtherRuntime() {
        RuntimeException ex = new RuntimeException("Something bad");

        ResponseEntity<ApiError> resp = handler.handleRuntime(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals(400, resp.getBody().getStatus());
        assertEquals("Something bad", resp.getBody().getMessage());
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("Unknown error");

        ResponseEntity<ApiError> resp = handler.handleGeneric(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertEquals(500, resp.getBody().getStatus());
        assertEquals("Unknown error", resp.getBody().getMessage());
    }
}

