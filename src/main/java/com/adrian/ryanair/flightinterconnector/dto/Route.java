package com.adrian.ryanair.flightinterconnector.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Route(
        @JsonProperty("airportFrom") String airportFrom,
        @JsonProperty("airportTo") String airportTo,
        @JsonProperty("connectingAirport") String connectingAirport,
        @JsonProperty("operator") String operator
) {}
