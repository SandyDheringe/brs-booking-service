package com.brsbooking.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
public class BusInventoryDto {
    private Integer id;
    private Integer busId;
    private Integer availableSeats;
}