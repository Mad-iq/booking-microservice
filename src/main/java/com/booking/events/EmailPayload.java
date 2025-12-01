package com.booking.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailPayload {
    private String email;
    private String name;
    private String pnr;
    private String flightId;
}
