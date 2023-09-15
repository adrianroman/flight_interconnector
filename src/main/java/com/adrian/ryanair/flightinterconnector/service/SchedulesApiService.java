package com.adrian.ryanair.flightinterconnector.service;

import com.adrian.ryanair.flightinterconnector.dto.MonthSchedule;
import com.adrian.ryanair.flightinterconnector.dto.YearSchedule;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class SchedulesApiService {

    private static final String API_URL = "https://services-api.ryanair.com/timtbl/3/schedules/{departure}/{arrival}/years/{year}/months/{month}";
    private final RestTemplate restTemplate;

    public SchedulesApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private MonthSchedule searchMonthSchedule(String departure, String arrival, int year, int month) {
        String formattedUrl = API_URL
                .replace("{departure}", departure)
                .replace("{arrival}", arrival)
                .replace("{year}", Integer.toString(year))
                .replace("{month}", Integer.toString(month));
        try {
            return restTemplate.getForObject(formattedUrl, MonthSchedule.class);
        }
        catch (Exception e) {
            return new MonthSchedule(0, Collections.emptyList());
        }

    }

    public List<YearSchedule> searchRangeSchedule(String departure, String arrival, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        int startYear = departureTime.getYear();
        int endYear = arrivalTime.getYear();
        return IntStream.rangeClosed(startYear, endYear)
                .mapToObj(year -> {
                    int endMonth = year == endYear ? arrivalTime.getMonthValue() : 12;
                    int startMonth = year == startYear ? departureTime.getMonthValue() : 1;
                    return searchYearSchedule(departure, arrival, year, startMonth, endMonth);
                })
                .toList();
    }
    private YearSchedule searchYearSchedule(String departure, String arrival, int year, int startMonth, int endMonth) {
        YearSchedule yearSchedule = new YearSchedule(year);

        IntStream.rangeClosed(startMonth, endMonth)
                .mapToObj(month -> searchMonthSchedule(departure, arrival, year, month))
                .forEach(yearSchedule::addMonth);

        return yearSchedule;
    }

}
