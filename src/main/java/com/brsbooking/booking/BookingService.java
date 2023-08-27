package com.brsbooking.booking;

import com.brsbooking.config.UserDetailConfig;
import com.brsbooking.exception.BRSException;
import com.brsbooking.exception.BRSResourceNotFoundException;
import com.brsbooking.search.BusRoute;
import com.brsbooking.search.BusRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
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

    private final JmsTemplate jmsTemplate;

    private final UserDetailConfig userDetailConfig;

    private final CustomerRepository customerRepository;
    @Autowired
    BookingService(WebClient.Builder webClientBuilder, BookingRepository bookingRepository,
                   BusRouteRepository busRouteRepository, PassengerRepository passengerRepository,
                   JmsTemplate jmsTemplate, UserDetailConfig userDetailConfig,
                   CustomerRepository customerRepository) {
        this.webClientBuilder = webClientBuilder;
        this.bookingRepository = bookingRepository;
        this.busRouteRepository = busRouteRepository;
        this.passengerRepository = passengerRepository;
        this.jmsTemplate = jmsTemplate;
        this.userDetailConfig = userDetailConfig;
        this.customerRepository = customerRepository;
    }

    public Mono<BusInventoryDto> fetchBusInventory(Integer busId) {
        return webClientBuilder
                .baseUrl(INVENTORY_HOST)
                .build()
                .get()
                .uri(GET_INVENTORIES, busId)
                .retrieve()
                .bodyToMono(BusInventoryDto.class);
    }


    public void doBusBooking(BookingRequestDto bookingRequestDto, String authorizationHeader) {

        Optional<BusInventoryDto> busInventoryDto = fetchBusInventory(bookingRequestDto.getBusId()).blockOptional();

        Customer customer = customerRepository.findByUserName(userDetailConfig.getUserInfo(authorizationHeader).getUsername()).orElse(null);

        if(customer == null){
            throw new BRSException("Customer not found");
        }

        if (busInventoryDto.isPresent()) {
            BusInventoryDto busInventoryDetail = busInventoryDto.get();
            BusRoute busRoute = busRouteRepository.findById(bookingRequestDto.getBusId()).orElse(null);
            if(busRoute == null){
                throw new BRSException("Something went wrong");
            }
            List<Passenger> passengers = bookingRequestDto.getPassengerDetails();

            if (busInventoryDetail.getAvailableSeats() >= passengers.size()) {
                Booking booking = new Booking();
                booking.setBusId(bookingRequestDto.getBusId());
                booking.setCustomerId(customer.getId());
                booking.setBookingDate(bookingRequestDto.getBookingDate());
                booking.setNoOfSeats(passengers.size());
                booking.setBookingStatus(BookingStatus.PENDING);
                booking.setTotalAmount(busRoute.getFareAmount()*booking.getNoOfSeats());
                passengers.forEach(passenger -> passenger.setBooking(booking));
                bookingRequestDto.setPassengerDetails(passengers);
                booking.setPassengers(passengers);
                Booking result = bookingRepository.saveAndFlush(booking);
                sendMessage("brsqueue","booking id="+result.getId());
            }
        } else {
            throw new BRSException("Something went wrong");
        }
    }

    public void sendMessage(String destination, String message) {
        jmsTemplate.send(destination, session -> {
            javax.jms.Message jmsMessage = session.createTextMessage(message);
            return jmsMessage;
        });
        System.out.println("Sent message: " + message);
    }

    public Optional<Booking> getBooking(Integer bookingId) {
        return bookingRepository.findById(bookingId);
    }

    public List<Booking> getBookings() {
        return bookingRepository.findAll();
    }

    public void cancelBooking(Integer bookingId) {
        Optional<Booking> bookingDetail = bookingRepository.findById(bookingId);
        if(bookingDetail.isPresent()){
            Booking booking =bookingDetail.get();
            booking.setBookingStatus(BookingStatus.CANCEL);
        }
        else{
            throw new BRSResourceNotFoundException(String.format("Booking details with id %d not found", bookingId));
        }

    }

    @JmsListener(destination = "brsqueue")
    public void receiveMessage(String message) {
        Booking bookingDetail = bookingRepository.findById(1).orElse(null);
        bookingDetail.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.saveAndFlush(bookingDetail);
    }
}
