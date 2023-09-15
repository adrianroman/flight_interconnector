package com.adrian.ryanair.flightinterconnector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalTime;

public record FlightInfo(@JsonProperty("carrierCode") String carrierCode,
                         @JsonProperty("number") String number,
                         @JsonProperty("departureTime")
                         @DateTimeFormat(pattern = "HH:mm")
                         LocalTime departureTime,
                         @JsonProperty("arrivalTime")
                         @DateTimeFormat(pattern = "HH:mm")
                         LocalTime arrivalTime
) { }
