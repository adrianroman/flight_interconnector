package com.adrian.ryanair.flightinterconnector.dto;

import java.util.List;

public record InterconnectedFlight(int stops, List<Leg> legs) {
}
