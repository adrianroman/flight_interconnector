package com.adrian.ryanair.flightinterconnector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MonthSchedule(@JsonProperty("month") int month,
                            @JsonProperty("days") List<FlightDay> days
) {}
