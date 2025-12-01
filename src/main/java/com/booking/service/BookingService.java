package com.booking.service;

import java.util.Map;
import com.booking.request.BookingRequest;

public interface BookingService {
    Map<String,Object> bookTicket(String flightId, BookingRequest req);
    Map<String,Object> getTicketByPnr(String pnr);
    Map<String,Object> getBookingHistory(String email);
    Map<String,Object> cancelBooking(String pnr);
}
