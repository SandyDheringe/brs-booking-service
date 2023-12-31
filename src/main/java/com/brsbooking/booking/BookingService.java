package com.brsbooking.booking;

import com.brsbooking.exception.BRSException;
import com.brsbooking.exception.BRSFieldException;
import com.brsbooking.exception.BRSResourceNotFoundException;
import com.brsbooking.messages.BookingMessage;
import com.brsbooking.messages.BusBookingMessage;
import com.brsbooking.messages.MessageBroker;
import com.brsbooking.messages.MessageDestinationConst;
import com.brsbooking.search.BusRoute;
import com.brsbooking.search.BusRouteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookingService {

    @Value("${endpoint.inventory.host}")
    private String INVENTORY_HOST;
    @Value("${endpoint.inventory.getInventories}")
    private String GET_INVENTORIES;

    private final WebClient.Builder webClientBuilder;

    private final BookingRepository bookingRepository;

    private final BusRouteRepository busRouteRepository;

    private final PassengerRepository passengerRepository;

    private final MessageBroker messageBroker;

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    private static final String INVENTORY_SERVICE = "inventoryService";


    @Autowired
    BookingService(WebClient.Builder webClientBuilder, BookingRepository bookingRepository,
                   BusRouteRepository busRouteRepository, PassengerRepository passengerRepository,
                   MessageBroker messageBroker, ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.webClientBuilder = webClientBuilder;
        this.bookingRepository = bookingRepository;
        this.busRouteRepository = busRouteRepository;
        this.passengerRepository = passengerRepository;
        this.messageBroker = messageBroker;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    //    @CircuitBreaker(name =INVENTORY_SERVICE,fallbackMethod = "fetchDefaultBusInventory")
//    @Retry(name = INVENTORY_SERVICE,fallbackMethod = "fetchDefaultBusInventory")
    public Optional<BusInventoryDto> fetchBusInventory(Integer busId) {
        System.out.println("API call fetchBusInventory");
        return webClientBuilder
                .baseUrl(INVENTORY_HOST)
                .build()
                .get()
                .uri(GET_INVENTORIES, busId)
                .retrieve()
                .bodyToMono(BusInventoryDto.class).blockOptional();
    }

    @CircuitBreaker(name = INVENTORY_SERVICE, fallbackMethod = "fetchDefaultBusInventory")
//    @Retry(name = INVENTORY_SERVICE, fallbackMethod = "fetchDefaultBusInventory")
    public Optional<BusInventoryDto> fetchBusInventoryOld(Integer busId) {
        System.out.println("API call fetchBusInventory");
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<BusInventoryDto> entity = new HttpEntity<>(headers);
        ResponseEntity<BusInventoryDto> res = restTemplate.exchange(INVENTORY_HOST + GET_INVENTORIES,
                HttpMethod.GET, entity, BusInventoryDto.class, busId);
        if (res.getStatusCode() == HttpStatus.OK && res.hasBody()) {
            return Optional.ofNullable(res.getBody());
        } else {
            return Optional.empty();
        }
    }

    public Optional<BusInventoryDto> fetchDefaultBusInventory(Exception e) {
        return Optional.empty();
    }

    public BookingResponseDto doBusBooking(BookingRequestDto bookingRequestDto) {

        Optional<BusInventoryDto> busInventoryDto = fetchBusInventory(bookingRequestDto.getBusId());

        if (busInventoryDto.isPresent()) {
            BusInventoryDto busInventoryDetail = busInventoryDto.get();
            BusRoute busRoute = busRouteRepository.findById(bookingRequestDto.getBusId()).orElse(null);
            if (busRoute == null) {
                throw new BRSException("BusRoute detail not found");
            }
            List<Passenger> passengers = bookingRequestDto.getPassengerDetails();

            if (busInventoryDetail.getAvailableSeats() >= passengers.size()) {
                Booking booking = new Booking();
                booking.setBusId(bookingRequestDto.getBusId());
                booking.setCustomerId(bookingRequestDto.getCustomerId());
                booking.setBookingDate(bookingRequestDto.getBookingDate());
                booking.setNoOfSeats(passengers.size());
                booking.setBookingStatus(BookingStatus.PENDING);
                booking.setTotalAmount(busRoute.getFareAmount() * booking.getNoOfSeats());
                passengers.forEach(passenger -> passenger.setBooking(booking));
                bookingRequestDto.setPassengerDetails(passengers);
                booking.setPassengers(passengers);
                Booking newBooking = bookingRepository.saveAndFlush(booking);
                messageBroker.sendBookingMessage(MessageDestinationConst.DEST_PROCESS_PAYMENT, new BookingMessage(newBooking.getId(), newBooking.getTotalAmount()));
                return new BookingResponseDto(BookingStatus.PENDING, newBooking.getId(), newBooking.getTotalAmount());
            } else {
                throw new BRSFieldException("Insufficient seats");
            }
        } else {
            throw new BRSException("Could not retrieve bus inventory");
        }
    }


    public Optional<Booking> getBooking(Integer bookingId) {
        return bookingRepository.findById(bookingId);
    }

    public List<Booking> getBookings(Integer customerId) {
        return bookingRepository.findAllByCustomerId(customerId);
    }

    public void cancelBooking(Integer bookingId) {
        Optional<Booking> bookingDetail = bookingRepository.findByIdAndBookingStatus(bookingId, BookingStatus.CONFIRMED);
        if (bookingDetail.isPresent()) {
            Booking booking = bookingDetail.get();
            booking.setBookingStatus(BookingStatus.CANCEL);
            bookingRepository.saveAndFlush(booking);
            messageBroker.sendBookingMessage(MessageDestinationConst.DEST_INITIATE_PAYMENT_REFUND, new BookingMessage(bookingId, null));
        } else {
            throw new BRSResourceNotFoundException(String.format("No Confirm Booking details with id %d found", bookingId));
        }
    }

    @JmsListener(destination = MessageDestinationConst.DEST_UPDATE_BOOKING)
    public void receiveMessage(Map<String, Object> object) {
        final BusBookingMessage busBookingMessage = objectMapper.convertValue(object, BusBookingMessage.class);
        System.out.println("Received message: " + busBookingMessage);
        Booking bookingDetail = bookingRepository.findById(busBookingMessage.getBookingId()).orElse(null);
        bookingDetail.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.saveAndFlush(bookingDetail);
    }
}
