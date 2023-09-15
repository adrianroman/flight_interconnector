# Flight interconnector
Spring Boot based RESTful API application which serves information about possible direct and interconnected flights

# Table of Contents

- [Project Overview](#project-overview)
- [Requirements](#requirements)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Compute Interconnected Flights Logic](#compute-interconnected-flights-logic)
- [DTOs (Data Transfer Objects)](#dtos-data-transfer-objects)
- [Exceptions](#exceptions)
- [Testing Strategy](#testing-strategy)
 - [Logging](#logging)
- [Building and Running](#building-and-running)

# Project Overview

This Spring Boot-based RESTful API application provides information about possible direct and interconnected flights (maximum 1 stop) based on data consumed from external APIs.

## Requirements

- Java 17 or later
- Maven (for building the application)

## Usage

This Spring Boot application provides a single endpoint that allows you to retrieve a list of flights departing from a specified departure airport no earlier than the provided departure datetime and arriving at a given arrival airport no later than the specified arrival datetime. The list includes the following:

-   All direct flights (e.g., DUB - WRO) if available.
-   All interconnected flights with a maximum of one stop (e.g., DUB - STN - WRO) if available. For interconnected flights, there should be a minimum 2-hour gap between the arrival and the next departure.

To use the application, you can make HTTP GET requests to the following endpoint:

`http://<HOST>/<CONTEXT>/interconnections?departure={departure}&arrival={arrival}&departureDateTime={departureDateTime}&arrivalDateTime={arrivalDateTime}`

-   `departure` - Departure airport IATA code.
-   `departureDateTime` - Departure datetime in the departure airport timezone in ISO format `yyyy-MM-dd'T'HH:mm`.
-   `arrival` - Arrival airport IATA code.
-   `arrivalDateTime` - Arrival datetime in the arrival airport timezone in ISO format `yyyy-MM-dd'T'HH:mm`.

### Example Request:

[http://localhost:8080/api/v1/interconnectionsdeparture=DUB&arrival=WRO&departureDateTime=2023-11-12T23:01:12&arrivalDateTime=2023-12-13T23:01:12](http://localhost:8080/api/v1/interconnections?departure=DUB&arrival=WRO&departureDateTime=2023-11-12T23:01:12&arrivalDateTime=2023-12-13T23:01:12)

You can replace the query parameters (`departure`, `arrival`, `departureDateTime`, and `arrivalDateTime`) in the example URL with your desired values to retrieve flight information based on your criteria.

### Example Response:

```json
[
    {
        "stops": 0,
        "legs": [
            {
                "departureAirport": "DUB",
                "arrivalAirport": "WRO",
                "departureDateTime": "2023-11-13T09:25:00",
                "arrivalDateTime": "2023-11-13T12:55:00"
            }
        ]
    },
    {
        "stops": 1,
        "legs": [
            {
                "departureAirport": "DUB",
                "arrivalAirport": "AGP",
                "departureDateTime": "2023-11-13T10:50:00",
                "arrivalDateTime": "2023-11-13T14:45:00"
            },
            {
                "departureAirport": "AGP",
                "arrivalAirport": "WRO",
                "departureDateTime": "2023-11-13T20:15:00",
                "arrivalDateTime": "2023-11-13T23:55:00"
            }
        ]
    }
]
```

## Project Structure


The project adheres to standard Spring Boot  web application conventions.

### Controllers

-   **FlightInterconnectorController**: Defines the endpoint route and the accepted parameter types. This controller exposes the endpoint.

### Services

Three services have been created:

-   **RouteAPIService**: Manages the logic for extracting data from the external routes API.
    
-   **ScheduleAPIService**: Manages the logic for extracting data from external schedule services. It allows the creation of a schedule within a specified time range, separated by years, months, and days. This service extracts schedule data from external schedule by month API.
    
-   **InterconnectorService**: Calculates interconnected flights based on the user's input, including origin and destination airports, departure and arrival dates. It utilizes two essential services:

- - **RouteAPIService**: This service fetches all available Ryanair routes by fetching an external API, providing a  list of routes formed by origin destiantion.
    
- -  **ScheduleAPIService**: Manages the logic for extracting data from external schedule services. It allows the creation of a schedule within a specified time range, separated by years, months, and days. This service extracts schedule data from an external api that returns the schedules for a route, by month.

## Compute Interconnected Flights Logic

The **InterconnectedFlightService** follows a systematic process to generate interconnected flight options, which includes both direct and interconnected flights:

  

1.  **Routes Acquisition**:

  

- Initially, the service obtains all available flight routes from the `RouteAPIService`. This step is performed once to gather a comprehensive list of potential flight paths.

  

2.  **Direct Flights Retrieval**:

  

- Call the `ScheduleAPIService` with the specified `origin` and `destination` airports.

  

- Specify the time range for schedules, encompassing the `departureDateTime` and `arrivalDateTime` to cover the desired travel period.

  

- The `ScheduleAPIService` will return a list of schedules for the direct flight route between the specified origin and destination airports, considering the provided time frame.

  

3.  **Interconnected Flights Computation**:

  

- The service first identifies all possible "first leg" flights. These flights depart from the user-specified origin airport (the same as the direct flight origin) but have random destinations different from the direct flight destination.

  

- For each "first leg" flight, the service calculates both the minimum and maximum allowable arrival times at the next airport. These calculations are based on the departure time of the "first leg" flight, with the inclusion of a minimum 2-hour layover time.

  

- Subsequently, the service queries the `ScheduleAPIService` again, this time with the "first leg" flight's destination airport as the new `origin` and the user-specified destination airport as the `destination`. The specified time range should align with the computed allowable arrival times.

  

- The schedules returned at this stage represent potential "second leg" flights that connect the "first leg" flight to the user's destination while adhering to the defined time constraints.

  

4.  **Flight Validation and Presentation**:

  

- The service proceeds to create and validate valid flight combinations and return them along the direct ones.

### DTOs (Data Transfer Objects)

1.  **FlightDay**
    
    -   Represents flight information for a specific day.
    -   Used for organizing flight data for a given day.
2.  **FlightInfo**
    
    -   Represents detailed flight information.
    -   Used for providing information about individual flights, including carrier code, flight number, departure, and arrival times.
3.  **InterconnectedFlight**
    
    -   Represents information about interconnected flights.
    -   Used for output of the endpoint, providing data about interconnected flights, including the number of stops and a list of flight legs.
4.  **Leg**
    
    -   Represents a flight leg, which is a portion of a flight journey.
    -   Contains information about the departure and arrival airports, as well as the departure and arrival times for a leg of a flight.
5.  **MonthSchedule**
    
    -   Represents a monthly schedule of flights.
    -   Used to organize flight data for a specific month, including flight days.
6.  **Route**
    
    -   Represents information about a flight route.
    -   Contains details such as the departure airport, arrival airport, connecting airport (if any), and operator.
7.  **YearSchedule**
    
    -   Represents a yearly schedule of flights.
    -   Used to organize flight data for an entire year, including monthly schedules.

### Exceptions

The project includes custom exceptions, including:

-   **InvalidDateException**: This custom exception is used to handle cases where invalid dates are encountered in the application's logic.

The central exception handler, **GlobalExceptionHandler**, plays a vital role in managing exceptions. It is configured to handle various exceptions, including:

-   **InvalidDateException**: Handles exceptions related to invalid dates, ensuring proper error responses are sent to clients.
    
-   **MethodArgumentTypeMismatchException**: Manages cases where there is a mismatch in argument types for API requests, providing appropriate error handling for these situations.
    

Additionally, the `GlobalExceptionHandler` has a default exception handling method defined for other types of exceptions that may occur in the application, ensuring that clients receive meaningful error responses for any unexpected scenarios.




## Testing Strategy

### Unit Testing (Services)

#### Service Unit Tests

- **RouteAPIService**: Unit tests for retrieving and processing route data from external routes api and validate data filtering.

- **ScheduleAPIService**: Unit tests for fetching and processing custom time ranges schedules from the external schedule api..

- **InterconnectedFlightService**: Unit tests for computing interconnected flight options based on provided inputs.

### Component Testing (Controllers)

Component testing validates the functionality of controllers and their interaction with services. These tests ensure that the API endpoints and request/response handling work correctly.

#### Controller Component Tests

- **FlightInterconnectorController**: Component tests for API endpoints, request handling, and response generation.

### Integration Testing


- **Application Integration Tests**: Comprehensive tests that simulate real-world scenario by testing the entire application, including the interaction between controllers and services and validating the output or expected errors.


## Logging

The application uses Java's built-in `java.util.logging` framework for logging. Logging messages are defined in the `application.properties` file.



## Building and Running
1. Clone the project repository.
2. Navigate to the project directory.
3. Build the application using Maven:
   mvn clean install

4. Run the application:

   `java -jar target/interconnecting-flights-<version>.jar`

   Example :  `java -jar target/flight_interconnector-0.0.1-SNAPSHOT.jar`

