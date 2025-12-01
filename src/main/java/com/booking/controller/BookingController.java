package com.booking.controller;

import java.util.Map;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking.request.BookingRequest;
import com.booking.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/flight")
@RequiredArgsConstructor
@EnableDiscoveryClient
public class BookingController {

 private final BookingService bookingService;

 @PostMapping("/booking/{flightId}")
 public ResponseEntity<?> bookTicket(@PathVariable String flightId, @Valid @RequestBody BookingRequest request) {
     Map<String,Object> resp = bookingService.bookTicket(flightId, request);
     return ResponseEntity.status(201).body(resp);
 }

 @GetMapping("/ticket/{pnr}")
 public ResponseEntity<?> getTicket(@PathVariable String pnr) {
     return ResponseEntity.ok(bookingService.getTicketByPnr(pnr));
 }

 @GetMapping("/booking/history/{emailId}")
 public ResponseEntity<?> getHistory(@PathVariable("emailId") String emailId) {
     return ResponseEntity.ok(bookingService.getBookingHistory(emailId));
 }

 @DeleteMapping("/booking/cancel/{pnr}")
 public ResponseEntity<?> cancelBooking(@PathVariable String pnr) {
     return ResponseEntity.ok(bookingService.cancelBooking(pnr));
 }
}

