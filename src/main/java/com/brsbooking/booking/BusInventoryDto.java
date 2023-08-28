package com.brsbooking.booking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusInventoryDto {
    private Integer id;
    private Integer busId;
    private Integer availableSeats;
}