package com.booking.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.booking.controller.BookingController;
import com.booking.request.BookingRequest;
import com.booking.request.PassengerRequest;
import com.booking.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private ObjectMapper mapper = new ObjectMapper();

    private BookingRequest sampleRequest() {
        BookingRequest r = new BookingRequest();
        r.setEmail("a@b.com"); r.setName("A"); r.setNumberOfSeats(1);
        PassengerRequest p = new PassengerRequest(); p.setName("P"); p.setGender("M"); p.setAge(30);
        r.setPassengers(List.of(p));
        r.setMealPreference("VEG");
        r.setSeatNumbers(List.of("1A"));
        return r;
    }

    @BeforeEach
    void setup() { }

    @Test
    void bookTicket_returns201() throws Exception {
        BookingRequest req = sampleRequest();
        when(bookingService.bookTicket(eq("F1"), any(BookingRequest.class))).thenReturn(Map.of("pnr","P1","message","Booking successful","totalPrice",100));

        mockMvc.perform(post("/api/flight/booking/F1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pnr").value("P1"));
    }

    @Test
    void getTicket_returns200() throws Exception {
        when(bookingService.getTicketByPnr("P1")).thenReturn(Map.of("pnr","P1","flightId","F1"));
        mockMvc.perform(get("/api/flight/ticket/P1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pnr").value("P1"));
    }

    @Test
    void getHistory_returns200() throws Exception {
        when(bookingService.getBookingHistory("a@b.com")).thenReturn(Map.of("email","a@b.com","history",List.of()));
        mockMvc.perform(get("/api/flight/booking/history/a@b.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("a@b.com"));
    }

    @Test
    void cancelBooking_returns200() throws Exception {
        when(bookingService.cancelBooking("P1")).thenReturn(Map.of("pnr","P1","message","Ticket cancelled successfully"));
        mockMvc.perform(delete("/api/flight/booking/cancel/P1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pnr").value("P1"));
    }
}

