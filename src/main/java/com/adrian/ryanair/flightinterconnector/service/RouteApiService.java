package com.adrian.ryanair.flightinterconnector.service;

import com.adrian.ryanair.flightinterconnector.dto.Route;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class RouteApiService {

    static final String API_URL = "https://services-api.ryanair.com/views/locate/3/routes";
    private final RestTemplate restTemplate;

    public RouteApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public List<Route> searchRyanairRoutes() {

        try {
            String jsonResponse = restTemplate.getForObject(API_URL, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            Route[] routesArray = objectMapper.readValue(jsonResponse, Route[].class);
            List<Route> rawRoutes = Arrays.asList(routesArray);
            return filterRoutes(rawRoutes);

        } catch (Exception e) {
            return Collections.emptyList();
        }

    }

    private List<Route> filterRoutes(List<Route> rawRoutes) {
        return rawRoutes.stream().filter(route -> route.connectingAirport() == null && "RYANAIR".equals(route.operator())).toList();
    }

}
