package com.adrian.ryanair.flightinterconnector.service;

import com.adrian.ryanair.flightinterconnector.dto.Route;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class RouteApiServiceTest {

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private RouteApiService routeApiService;
    private final String API_URL = "https://services-api.ryanair.com/views/locate/3/routes";


    @Test
    void searchRoutes() throws JsonProcessingException {
        Route[] mockRoutes = getMockRoutes();

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String jsonArray = objectWriter.writeValueAsString(mockRoutes);
        when(restTemplate.getForObject(API_URL, String.class)).thenReturn(jsonArray);
        List<Route> result = routeApiService.searchRyanairRoutes();

        List<Route> expectedRoutes = getExpectedRoutes();
        Assertions.assertArrayEquals(expectedRoutes.toArray(), result.toArray());
    }

    private static List<Route> getExpectedRoutes() {
        return Arrays.asList(
                new Route("DUB", "BCN", null, "RYANAIR"),
                new Route("BCN", "WRO", null, "RYANAIR"),
                new Route("DUB", "WRO", null, "RYANAIR")
        );
    }

    private static Route[] getMockRoutes() {
        return new Route[]{
                new Route("DUB", "BCN", null, "RYANAIR"),
                new Route("BCN", "WRO", null, "RYANAIR"),
                new Route("DUB", "WRO", null, "RYANAIR"),
                new Route("DUB", "MAD", "NCE", "RYANAIR"),
                new Route("DUB", "MAD", null, "SOME_OTHER_AIRLINE")
        };
    }
}