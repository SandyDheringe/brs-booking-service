package com.brsbooking.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Optional<Booking> findByIdAndBookingStatus(Integer id, BookingStatus bookingStatus);


    List<Booking> findAllByBookingStatus(BookingStatus bookingStatus);

    List<Booking> findAllByCustomerId(Integer customerId);
}