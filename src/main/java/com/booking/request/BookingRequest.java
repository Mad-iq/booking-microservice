package com.booking.request;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BookingRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @Min(1)
    private int numberOfSeats;

    @NotEmpty
    private List<PassengerRequest> passengers;

    @NotBlank
    private String mealPreference; 

    @NotEmpty
    private List<String> seatNumbers;
}
