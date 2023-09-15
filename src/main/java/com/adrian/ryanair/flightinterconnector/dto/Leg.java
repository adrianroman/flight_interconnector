package com.adrian.ryanair.flightinterconnector.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record Leg(String departureAirport, String arrivalAirport,
                  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
                  LocalDateTime departureDateTime,
                  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
                  LocalDateTime arrivalDateTime
) {}
