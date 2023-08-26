package com.brsbooking.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bus_route", schema = "bus_reservation_db")
public class BusRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bus_id", nullable = false)
    private Integer id;

    @Column(name = "bus_number", length = 20)
    private String busNumber;

    @Column(name = "bus_type", length = 20)
    private String busType;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @Column(name = "source", nullable = false, length = 100)
    private String source;

    @Column(name = "destination", nullable = false, length = 100)
    private String destination;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "fare_amount")
    private Float fareAmount;

    @JsonIgnore
    @Column(name = "is_deleted")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isDeleted = false;

}