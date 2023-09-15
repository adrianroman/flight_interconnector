package com.adrian.ryanair.flightinterconnector.service;

import com.adrian.ryanair.flightinterconnector.TestUtils;
import com.adrian.ryanair.flightinterconnector.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterconnectionServiceTest {

    @Mock
    private RouteApiService routeApiService;

    @Mock
    private SchedulesApiService schedulesApiService;

    @InjectMocks
    private InterconnectionService interconnectionService;


    @Test
    void testSearchFlights() {
        List<Route> routes = prepareRoutes();
        List<YearSchedule> yearSchedules = prepareYearSchedules();
        LocalDateTime departureDateTime = LocalDateTime.of(2022, 11, 1, 10, 34, 2);
        LocalDateTime arrivalDateTime = departureDateTime.plusYears(2).plusDays(1);

        when(routeApiService.searchRyanairRoutes()).thenReturn(routes);
        when(schedulesApiService.searchRangeSchedule(any(), any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(yearSchedules);

        List<InterconnectedFlight> interconnectedFlights =
                interconnectionService.searchFlights("DUB", "WRO", arrivalDateTime, departureDateTime);

        assert (!interconnectedFlights.isEmpty());
        assert (TestUtils.validateInterconectedFlights(departureDateTime, arrivalDateTime, interconnectedFlights));
    }

    private List<Route> prepareRoutes() {
        List<Route> routes = new ArrayList<>();
        routes.add(new Route("DUB", "BCN", null, null));
        routes.add(new Route("BCN", "WRO", null, null));
        routes.add(new Route("DUB", "WRO", null, null));
        routes.add(new Route("DUB", "MAD", null, null));
        routes.add(new Route("MAD", "BCN", null, null));
        return routes;
    }

    private List<YearSchedule> prepareYearSchedules() {
        List<YearSchedule> yearSchedules = new ArrayList<>();
        YearSchedule yearSchedule = new YearSchedule(2023);

        IntStream.range(0, 4).forEach(i -> {
            LocalTime departureTime = LocalTime.of(7, 25);
            LocalTime arrivalTime = LocalTime.of(12, 30);

            MonthSchedule monthSchedule1 = createMonthSchedule(11, departureTime, arrivalTime,i);
            MonthSchedule monthSchedule2 = createMonthSchedule(12, departureTime, arrivalTime,i);

            yearSchedule.addMonth(monthSchedule1);
            yearSchedule.addMonth(monthSchedule2);
        });

        yearSchedules.add(yearSchedule);
        return yearSchedules;
    }

    private MonthSchedule createMonthSchedule(int month, LocalTime departureTime, LocalTime arrivalTime, int day) {
        return new MonthSchedule(month, List.of(new FlightDay(day, List.of(new FlightInfo("", "", departureTime, arrivalTime)))));
    }


}
