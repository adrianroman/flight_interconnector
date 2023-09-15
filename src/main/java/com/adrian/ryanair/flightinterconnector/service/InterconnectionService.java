package com.adrian.ryanair.flightinterconnector.service;

import com.adrian.ryanair.flightinterconnector.dto.*;
import com.adrian.ryanair.flightinterconnector.exception.InvalidDateException;
import com.adrian.ryanair.flightinterconnector.utility.Util;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
public class InterconnectionService {

    RouteApiService routeApiService;
    SchedulesApiService schedulesApiService;


    public InterconnectionService(SchedulesApiService schedulesApiService, RouteApiService routeApiService) {
        this.schedulesApiService = schedulesApiService;
        this.routeApiService = routeApiService;
    }

    public List<InterconnectedFlight> searchFlights(String departure, String arrival, LocalDateTime arrivalDateTime,
                                                    LocalDateTime departureDateTime) {
        validateDates(departureDateTime,arrivalDateTime);

        List<Route> routes = routeApiService.searchRyanairRoutes();

        List<InterconnectedFlight> directFlights = searchDirectFlights(departure, arrival, arrivalDateTime, departureDateTime, routes);
        List<InterconnectedFlight> oneStopFlights = searchOneStopFlights(departure, arrival, arrivalDateTime, departureDateTime, routes);

        List<InterconnectedFlight> interconnectedFlights = new ArrayList<>(directFlights);
        interconnectedFlights.addAll(oneStopFlights);

        return interconnectedFlights;
    }

    private void validateDates(LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        if (arrivalDateTime.isBefore(departureDateTime)) {
            throw new InvalidDateException("Arrival date must be after departure date.");
        }
    }


    private List<Leg> findPotentialFirstLegs(List<Route> routes, String departure,
                                             LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {

        return routes.stream()
                .filter(route -> route.airportFrom().equals(departure))
                .map(route -> computeLegsByRoute(departureDateTime, arrivalDateTime, route))
                .flatMap(List::stream)
                .toList();
    }


    private List<InterconnectedFlight> searchOneStopFlights(String departure, String arrival,
                                                            LocalDateTime arrivalDateTime,
                                                            LocalDateTime departureDateTime, List<Route> routes) {

        List<Leg> potentialFirstLegs = findPotentialFirstLegs(routes, departure, departureDateTime, arrivalDateTime);

        List<Route> potentialSecondLegRoutes = routes.stream().filter(route -> route.airportTo().equals(arrival)).toList();
        Map<String, List<Route>> routesByAirportMap = buildMapRoutesByAirport(potentialSecondLegRoutes);


        return potentialFirstLegs.stream()
                .flatMap(firstLeg -> {
                    List<Route> secondLegsRoutes = routesByAirportMap.getOrDefault(firstLeg.arrivalAirport(), Collections.emptyList());
                    List<Leg> secondLegs = createSecondLegsByRoutes(arrivalDateTime, firstLeg, secondLegsRoutes);
                    return createInterconnectedFlightStream(firstLeg, secondLegs);
                })
                .toList();

    }

    private static Stream<InterconnectedFlight> createInterconnectedFlightStream(Leg firstLeg, List<Leg> secondLegs) {
        return secondLegs.stream()
                .map(secondLeg -> new InterconnectedFlight(1, List.of(firstLeg, secondLeg)));
    }

    private List<Leg> createSecondLegsByRoutes(LocalDateTime arrivalDateTime, Leg firstLeg, List<Route> secondLegRoutes) {
        return secondLegRoutes.stream()
                .map(route -> computeLegsByRoute(firstLeg.arrivalDateTime().plusHours(2), arrivalDateTime, route))
                .flatMap(List::stream)
                .toList();
    }

    private List<InterconnectedFlight> searchDirectFlights(String departure, String arrival,
                                                           LocalDateTime arrivalDateTime,
                                                           LocalDateTime departureDateTime, List<Route> routes) {

        List<InterconnectedFlight> directFlights = new ArrayList<>();
        Optional<Route> directRoute = routes.stream()
                .filter(route -> route.airportFrom().equals(departure) && route.airportTo().equals(arrival))
                .findFirst();

        List<YearSchedule> yearSchedules = schedulesApiService.
                searchRangeSchedule(departure, arrival, departureDateTime, arrivalDateTime);

        if (directRoute.isPresent()) {
            List<Leg> directLegs =
                    computeLegsForYears(directRoute.get(), yearSchedules, arrivalDateTime, departureDateTime);

            directFlights.addAll(createFlightsFromLegs(0, directLegs));
        }
        return directFlights;

    }

    private static List<InterconnectedFlight> createFlightsFromLegs(int stops, List<Leg> directLegs) {
        return directLegs.stream()
                .map(directLeg -> new InterconnectedFlight(stops, List.of(directLeg)))
                .toList();
    }


    private Map<String, List<Route>> buildMapRoutesByAirport(List<Route> routes) {
        Map<String, List<Route>> routesByAirport = new HashMap<>();
        routes.forEach(route -> routesByAirport.computeIfAbsent(route.airportFrom(), k -> new ArrayList<>()).add(route));
        return routesByAirport;
    }

    private List<Leg> computeLegsByRoute(LocalDateTime departureDateTime, LocalDateTime arrivalDateTime, Route route) {

        List<YearSchedule> yearSchedules = schedulesApiService
                .searchRangeSchedule(route.airportFrom(), route.airportTo(), departureDateTime, arrivalDateTime);

        return computeLegsForYears(route,
                yearSchedules,
                arrivalDateTime,
                departureDateTime);
    }

    private List<Leg> computeLegsForYears(Route route, List<YearSchedule> yearSchedules, LocalDateTime toTime,
                                         LocalDateTime fromTime) {

        return yearSchedules.stream()
                .flatMap(yearSchedule -> yearSchedule.getMonths().stream()
                        .flatMap(monthSchedule ->
                                createLegsForMonth(monthSchedule, yearSchedule.getYear(), route, fromTime, toTime)
                                        .stream()
                        )
                )
                .toList();
    }

    private List<Leg> createLegsForMonth(MonthSchedule monthSchedule, int year, Route route,
                                         LocalDateTime fromTime, LocalDateTime toTime) {
        int month = monthSchedule.month();
        return monthSchedule.days().stream()
                .filter(flightDay -> {
                    int day = flightDay.day();
                    return day >= fromTime.getDayOfMonth() && day <= toTime.getDayOfMonth();
                })
                .flatMap(flightDay -> createLegsForFlightDay(year, route, fromTime, toTime, month, flightDay, flightDay.day()).stream())
                .toList();
    }

    private List<Leg> createLegsForFlightDay(int year, Route route, LocalDateTime fromTime, LocalDateTime toTime, int month, FlightDay flightDay, int day) {
        return flightDay.flights().stream()
                .flatMap(flightInfo ->  createLegIfValidFlightInfo(year, route, fromTime, toTime, month, day, flightInfo).stream())
                .toList();
    }

    private Optional<Leg> createLegIfValidFlightInfo(int year, Route route, LocalDateTime fromTime, LocalDateTime toTime, int month, int day, FlightInfo flightInfo) {

        LocalDateTime realArrivalTime = Util.timeToDate(year, month, day, flightInfo.arrivalTime());
        boolean arrivalNextDay = flightInfo.departureTime().isAfter(flightInfo.arrivalTime());

        if (arrivalNextDay) {
            realArrivalTime = realArrivalTime.plusDays(1);
        }
        LocalDateTime realDepartureTime = Util.timeToDate(year, month, day, flightInfo.departureTime());
        boolean isTimeConsistent = realDepartureTime.isAfter(fromTime) && realArrivalTime.isBefore(toTime);

        if (isTimeConsistent) {
            return Optional.of(createLeg(route, realDepartureTime, realArrivalTime));
        }
        return Optional.empty();
    }

    private Leg createLeg(Route route, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        return new Leg(
                route.airportFrom(),
                route.airportTo(),
                departureTime,
                arrivalTime
        );
    }

}
