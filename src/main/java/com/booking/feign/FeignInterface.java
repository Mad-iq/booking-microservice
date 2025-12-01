package com.booking.feign;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "FLIGHT-MICROSERVICE")
public interface FeignInterface {

    @GetMapping("/api/flight/inventory/{flightId}")
    Map<String, Object> getFlightInfo(@PathVariable("flightId") String flightId);

    @PostMapping("/api/flight/inventory/reserve/{flightId}")
    Map<String, Object> reserveSeats(@PathVariable("flightId") String flightId, @RequestBody Map<String, Object> body);

    @PostMapping("/api/flight/inventory/release/{flightId}")
    Map<String, Object> releaseSeats(@PathVariable("flightId") String flightId, @RequestBody Map<String, Object> body);
}
