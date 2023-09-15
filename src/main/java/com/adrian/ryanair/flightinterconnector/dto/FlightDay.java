package com.adrian.ryanair.flightinterconnector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public record FlightDay(
        @JsonProperty("day") int day,
        @JsonProperty("flights")
        List<FlightInfo> flights
) { }
