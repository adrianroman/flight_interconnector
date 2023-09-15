package com.adrian.ryanair.flightinterconnector.service;

import com.adrian.ryanair.flightinterconnector.dto.FlightDay;
import com.adrian.ryanair.flightinterconnector.dto.FlightInfo;
import com.adrian.ryanair.flightinterconnector.dto.MonthSchedule;
import com.adrian.ryanair.flightinterconnector.dto.YearSchedule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulesApiServiceTest {

    @InjectMocks
    private SchedulesApiService schedulesApiService;

    @Mock
    private RestTemplate restTemplate;

    @Test
    void testSearchRangeSchedule() {
        List<YearSchedule> mockedYearSchedules = createMockedYearSchedules();
        LocalDateTime departure = LocalDateTime.of(2022, 12, 1, 1, 1, 1);
        LocalDateTime arrival = LocalDateTime.of(2023, 1, 30, 1, 1, 1);

        when(restTemplate.getForObject(anyString(), eq(MonthSchedule.class))).thenReturn(createMockMonthSchedule());

        List<YearSchedule> result = schedulesApiService.searchRangeSchedule("DUB", "WRO", departure, arrival);

        Assertions.assertEquals(mockedYearSchedules, result);
    }

    private List<YearSchedule> createMockedYearSchedules() {
        List<YearSchedule> yearSchedules = new ArrayList<>();

        YearSchedule yearSchedule1 = new YearSchedule(2022);
        FlightDay flightDay1 = new FlightDay(15, createMockFlightInfos());
        MonthSchedule monthSchedule1 = new MonthSchedule(12, List.of(flightDay1));
        yearSchedule1.addMonth(monthSchedule1);
        yearSchedules.add(yearSchedule1);

        YearSchedule yearSchedule2 = new YearSchedule(2023);
        MonthSchedule monthSchedule2 = new MonthSchedule(12, List.of(flightDay1));
        yearSchedule2.addMonth(monthSchedule2);
        yearSchedules.add(yearSchedule2);

        return yearSchedules;
    }

    private List<FlightInfo> createMockFlightInfos() {
        List<FlightInfo> flightInfos = new ArrayList<>();

        LocalTime departureTime = LocalTime.of(7, 25);
        LocalTime arrivalTime = LocalTime.of(9, 25);
        FlightInfo flightInfo = new FlightInfo("", "", departureTime, arrivalTime);

        flightInfos.add(flightInfo);

        return flightInfos;
    }

    private MonthSchedule createMockMonthSchedule() {

        FlightDay flightDay = new FlightDay(15, createMockFlightInfos());

        return new MonthSchedule(12, List.of(flightDay));
    }

}
