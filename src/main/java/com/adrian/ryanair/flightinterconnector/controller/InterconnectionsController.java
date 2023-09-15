package com.adrian.ryanair.flightinterconnector.controller;

import com.adrian.ryanair.flightinterconnector.dto.InterconnectedFlight;
import com.adrian.ryanair.flightinterconnector.service.InterconnectionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/interconnections")
public class InterconnectionsController {

    InterconnectionService interconnectionService;
    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm";
    private static final Logger log = Logger.getLogger(InterconnectionsController.class.getName());
    @Value("${custom.log.messages.receivedParameters}")
    private String receivedParametersMessage;

    @Value("${custom.log.messages.computingFlights}")
    private String computingFlightsMessage;

    @Value("${custom.log.messages.numberOfFlightsFound}")
    private String numberOfFlightsFoundMessage;

    public InterconnectionsController(InterconnectionService interconnectionService) {
        this.interconnectionService = interconnectionService;
    }

    @GetMapping()
    public ResponseEntity<List<InterconnectedFlight>> searchFlights(
            @RequestParam String departure,
            @RequestParam @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime departureDateTime,
            @RequestParam String arrival,
            @RequestParam @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime arrivalDateTime) {

        String receivedParameters = String.format(receivedParametersMessage,
                departure, arrival, departureDateTime, arrivalDateTime
        );
        log.info(receivedParameters);
        log.info(computingFlightsMessage);

        List<InterconnectedFlight> interconnectedFlights
                = interconnectionService.searchFlights(departure, arrival, arrivalDateTime, departureDateTime);

        String numberOfFlightsFound = String.format(
                numberOfFlightsFoundMessage,
                interconnectedFlights.size());
        log.info(numberOfFlightsFound);

        return new ResponseEntity<>(interconnectedFlights, HttpStatus.OK);
    }

}

