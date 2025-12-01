package com.booking.events;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailPayload implements Serializable {
    private String email;
    private String name;
    private String pnr;
    private String flightId;
}
