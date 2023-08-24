package com.brsbooking.search;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "route", schema = "bus_reservation_db")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @Column(name = "source", length = 100)
    private String source;

    @Column(name = "destination", length = 100)
    private String destination;

    @Column(name = "distance")
    private Integer distance;

    @Column(name = "duration", length = 20)
    private String duration;

    @Column(name = "fare_amount")
    private Float fareAmount;

    @Column(name = "is_deleted")
    private Byte isDeleted;

}