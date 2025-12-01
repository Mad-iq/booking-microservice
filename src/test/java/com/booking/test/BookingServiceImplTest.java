package com.booking.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.booking.model.Booking;
import com.booking.model.BookingStatus;
import com.booking.model.Passenger;
import com.booking.repositories.BookingRepository;
import com.booking.request.BookingRequest;
import com.booking.request.PassengerRequest;
import com.booking.service.BookingServiceImpl;

class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepo;

    @Mock
    private com.booking.feign.FeignInterface flightClient;

    @Mock
    private com.booking.publisher.EmailPublisher emailPublisher;

    @InjectMocks
    private BookingServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private BookingRequest sampleRequest() {
        BookingRequest req = new BookingRequest();
        req.setEmail("a@b.com");
        req.setName("Alice");
        req.setNumberOfSeats(2);
        PassengerRequest p1 = new PassengerRequest();
        p1.setName("P1"); p1.setGender("M"); p1.setAge(30);
        PassengerRequest p2 = new PassengerRequest();
        p2.setName("P2"); p2.setGender("F"); p2.setAge(25);
        req.setPassengers(List.of(p1, p2));
        req.setMealPreference("VEG");
        req.setSeatNumbers(List.of("1A", "1B"));
        return req;
    }

    @Test
    void bookTicket_shouldThrow_whenPassengerCountMismatch() {
        BookingRequest req = sampleRequest();
        req.setPassengers(List.of()); // empty
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.bookTicket("F1", req));
        assertTrue(ex.getMessage().contains("numberOfSeats"));
    }

    @Test
    void bookTicket_shouldThrow_whenSeatNumbersMismatch() {
        BookingRequest req = sampleRequest();
        req.setSeatNumbers(List.of("1A")); // only 1
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.bookTicket("F1", req));
        assertTrue(ex.getMessage().contains("Seat numbers count"));
    }

    @Test
    void bookTicket_shouldReturnFailed_whenFlightServiceFallback() {
        BookingRequest req = sampleRequest();
        when(flightClient.getFlightInfo("F1")).thenThrow(new RuntimeException("down"));
        Map<String,Object> resp = service.bookTicket("F1", req);
        assertEquals("FAILED", resp.get("status"));
        assertTrue(String.valueOf(resp.get("message")).toLowerCase().contains("unavailable"));
    }

    @Test
    void bookTicket_shouldReturnFailed_whenSeatsUnavailable() {
        BookingRequest req = sampleRequest();
        when(flightClient.getFlightInfo("F1")).thenReturn(Map.of(
            "fallback", false,
            "availableSeatNumbers", List.of("2A","2B"),
            "price", 100
        ));
        Map<String,Object> resp = service.bookTicket("F1", req);
        assertEquals("FAILED", resp.get("status"));
        assertTrue(String.valueOf(resp.get("message")).toLowerCase().contains("unavailable"));
    }

    @Test
    void bookTicket_shouldReturnFailed_whenReserveFallback() {
        BookingRequest req = sampleRequest();
        when(flightClient.getFlightInfo("F1")).thenReturn(Map.of(
            "fallback", false,
            "availableSeatNumbers", List.of("1A","1B"),
            "price", 50
        ));
        when(flightClient.reserveSeats(eq("F1"), anyMap())).thenThrow(new RuntimeException("down"));
        Map<String,Object> resp = service.bookTicket("F1", req);
        assertEquals("FAILED", resp.get("status"));
        assertTrue(String.valueOf(resp.get("message")).toLowerCase().contains("failed"));
    }

    @Test
    void bookTicket_shouldReturnFailed_whenReserveReturnsNonReserved() {
        BookingRequest req = sampleRequest();
        when(flightClient.getFlightInfo("F1")).thenReturn(Map.of(
            "fallback", false,
            "availableSeatNumbers", List.of("1A","1B"),
            "price", 40
        ));
        when(flightClient.reserveSeats(eq("F1"), anyMap())).thenReturn(Map.of("message", "Something else"));
        Map<String,Object> resp = service.bookTicket("F1", req);
        assertEquals("FAILED", resp.get("status"));
    }

    @Test
    void bookTicket_shouldSucceed_andSaveBooking_andPublishEmail() {
        BookingRequest req = sampleRequest();
        when(flightClient.getFlightInfo("F1")).thenReturn(Map.of(
            "fallback", false,
            "availableSeatNumbers", List.of("1A","1B","1C"),
            "price", 200
        ));
        when(flightClient.reserveSeats(eq("F1"), anyMap())).thenReturn(Map.of("message", "Reserved"));

        doAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(100L);
            return b;
        }).when(bookingRepo).save(any(Booking.class));

        Map<String,Object> resp = service.bookTicket("F1", req);

        assertTrue(resp.containsKey("pnr"));
        assertEquals("Booking successful", resp.get("message"));
        assertTrue(((Number)resp.get("totalPrice")).doubleValue() > 0);

        verify(bookingRepo, times(1)).save(any(Booking.class));
        verify(emailPublisher, times(1)).publishEmailEvent(any());
    }

    @Test
    void getTicketByPnr_shouldReturn_whenFound() {
        Booking b = new Booking();
        b.setPnr("PNR1"); b.setFlightId("F1"); b.setStatus(BookingStatus.CONFIRMED);
        b.setSeatNumbers(List.of("1A")); b.setPassengers(List.of(new Passenger()));
        when(bookingRepo.findByPnr("PNR1")).thenReturn(b);

        Map<String,Object> resp = service.getTicketByPnr("PNR1");
        assertEquals("PNR1", resp.get("pnr"));
    }

    @Test
    void getTicketByPnr_shouldThrow_whenNotFound() {
        when(bookingRepo.findByPnr("X")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getTicketByPnr("X"));
        assertTrue(ex.getMessage().contains("Booking not found"));
    }

    @Test
    void getBookingHistory_shouldReturnList() {
        Booking b = new Booking();
        b.setPnr("P1"); b.setFlightId("F1"); b.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepo.findByEmail("a@b.com")).thenReturn(List.of(b));
        Map<String,Object> resp = service.getBookingHistory("a@b.com");
        assertEquals("a@b.com", resp.get("email"));
        assertTrue(((List<?>)resp.get("history")).size() == 1);
    }

    @Test
    void cancelBooking_shouldThrow_whenNotFound() {
        when(bookingRepo.findByPnr("X")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.cancelBooking("X"));
        assertTrue(ex.getMessage().contains("Booking not found"));
    }

    @Test
    void cancelBooking_shouldSucceed_andAttemptRelease() {
        Booking b = new Booking();
        b.setPnr("P1"); b.setFlightId("F1"); b.setStatus(BookingStatus.CONFIRMED);
        b.setSeatNumbers(List.of("1A"));
        when(bookingRepo.findByPnr("P1")).thenReturn(b);
        when(flightClient.releaseSeats(eq("F1"), anyMap())).thenThrow(new RuntimeException("down"));

        doAnswer(invocation -> {
            Booking saved = invocation.getArgument(0);
            return saved;
        }).when(bookingRepo).save(any(Booking.class));

        Map<String,Object> resp = service.cancelBooking("P1");
        assertEquals("P1", resp.get("pnr"));
        assertTrue(String.valueOf(resp.get("message")).toLowerCase().contains("cancelled"));
        verify(bookingRepo, times(1)).save(any(Booking.class));
    }
}
