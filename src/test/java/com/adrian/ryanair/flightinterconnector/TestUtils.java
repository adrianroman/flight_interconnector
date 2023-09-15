package com.adrian.ryanair.flightinterconnector;

import com.adrian.ryanair.flightinterconnector.dto.InterconnectedFlight;
import com.adrian.ryanair.flightinterconnector.dto.Leg;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TestUtils {

    public static boolean validateInterconnectedFlightsResponse(String contentAsString, LocalDateTime departureDateTime,
                                                                LocalDateTime arrivalDateTime) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            List<InterconnectedFlight> response =
                    objectMapper.readValue(contentAsString, new TypeReference<>() {
                    });
            return validateInterconectedFlights(departureDateTime, arrivalDateTime, response);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean validateInterconectedFlights(LocalDateTime departureDateTime, LocalDateTime arrivalDateTime,
                                                       List<InterconnectedFlight> interconnectedFlights) {

        boolean validNStops = interconnectedFlights.stream().allMatch(flight ->flight.stops() <= 2);
        boolean validLegs = interconnectedFlights.stream()
                .allMatch(flight ->isValidFlight(flight, departureDateTime, arrivalDateTime));
        return validNStops && validLegs;
    }


    public static boolean isValidFlight(InterconnectedFlight flight, LocalDateTime departureDateTime,
                                        LocalDateTime arrivalDateTime) {

        if(flight.legs() == null || flight.legs().isEmpty()) return false;
        boolean  hasValidLegs = areValidLegs(flight.legs());
        boolean  hasValidStopDuration = legHasValidStopDuration(flight);
        boolean isOnTime = flight.legs().stream().allMatch(leg -> leg.departureDateTime().isAfter(departureDateTime)
        && leg.arrivalDateTime().isBefore(arrivalDateTime));

        return hasValidStopDuration && hasValidLegs && isOnTime;

    }

    private static boolean areValidLegs(List<Leg> legs) {
        if(legs == null || legs.isEmpty()) return false;
        Boolean timeConsistent = legs.stream().allMatch(leg -> leg.arrivalDateTime().isAfter(leg.departureDateTime()));
        if(legs.size()==1) return true;
        Boolean ordered = legs.get(0).arrivalDateTime().isBefore(legs.get(1).departureDateTime())
                || legs.get(0).arrivalAirport().equals(legs.get(1).departureAirport()) ;
      return timeConsistent && ordered;
    }



    private static boolean legHasValidStopDuration(InterconnectedFlight flight) {
        if(flight.legs().size() < 2) return true;
        LocalDateTime arrivalDateTime1 = flight.legs().get(0).arrivalDateTime();
        LocalDateTime departureDateTime2 = flight.legs().get(1).departureDateTime();
        long hoursDifference = java.time.Duration.between(arrivalDateTime1, departureDateTime2).toHours();
        return hoursDifference >= 2;
    }

    public static String formatDate(LocalDateTime localDateTime) {
        if(localDateTime == null) return "";
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    }
}
