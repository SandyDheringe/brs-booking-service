package com.brsbooking.booking;

import com.brsbooking.exception.BRSResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1")
public class BookingController {


    private final BookingService bookingService;

    @Autowired
    BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    public ResponseEntity doBusBooking(@RequestBody BookingRequestDto bookingRequestDto,
                                       @RequestHeader("Authorization") String authorizationHeader) {
        bookingService.doBusBooking(bookingRequestDto,authorizationHeader);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bookings/{booking_id}")
    public ResponseEntity<Booking> getBooking(@PathVariable("booking_id") Integer bookingId) {
        Optional<Booking> booking = bookingService.getBooking(bookingId);

        if (booking.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(booking.get());
        } else {
            throw new BRSResourceNotFoundException(String.format("Booking details with id %d not found", bookingId));
        }
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getBookings() {
        List<Booking> bookings = bookingService.getBookings();
        return ResponseEntity.status(HttpStatus.OK).body(bookings);
    }

    @DeleteMapping("/bookings/{booking_id}")
    public ResponseEntity cancelBooking(@PathVariable("booking_id") Integer bookingId){
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
