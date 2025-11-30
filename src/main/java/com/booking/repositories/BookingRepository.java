package com.booking.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.booking.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findByPnr(String pnr);
    List<Booking> findByEmail(String email);
}
