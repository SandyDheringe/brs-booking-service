package com.brsbooking.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {
    private BookingStatus bookingStatus;
    private Integer bookingId;
    private Float bookingAmount;
}



