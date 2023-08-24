package com.brsbooking.search;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "bus", schema = "bus_reservation_db")
public class Bus {
    @Id
    @Column(name = "bus_id", nullable = false)
    private Integer id;

    @Column(name = "bus_number", length = 20)
    private String busNumber;

    @Column(name = "bus_type", length = 20)
    private String busType;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @Column(name = "is_deleted")
    private Byte isDeleted;

}