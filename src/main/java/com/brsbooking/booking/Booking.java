package com.brsbooking.booking;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "booking", schema = "bus_reservation_db")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id", nullable = false)
    private Integer id;

    @Column(name = "booking_date")
    private Instant bookingDate;

    @Column(name = "no_of_seats")
    private Integer noOfSeats;

    @Column(name = "booking_status", length = 10)
    private String bookingStatus;

    @Column(name = "total_amount")
    private Float totalAmount;

}