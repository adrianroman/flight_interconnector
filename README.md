# Flight interconnector
Spring Boot based RESTful API application which serves information about possible direct and interconnected flights
## Project Overview

This Spring Boot-based RESTful API application provides information about possible direct and interconnected flights (maximum 1 stop) based on data consumed from external APIs.

## Requirements

- Java 17 or later
- Maven (for building the application)

## Usage

To use the application, you can make HTTP GET requests to the following endpoint:

    http://<HOST>/<CONTEXT>/interconnections?departure={departure}&arrival={arrival}&departureDateTime={departureDateTime}&arrivalDateTime={arrivalDateTime}

- `departure` - Departure airport IATA code
- `departureDateTime` - Departure datetime in the departure airport timezone in ISO format `yyyy-MM-dd'T'HH:mm`
- `arrival` - Arrival airport IATA code
- `arrivalDateTime` - Arrival datetime in the arrival airport timezone in ISO format `yyyy-MM-dd'T'HH:mm`

Example request:
[URL](http://localhost:8080/api/v1/interconnections?departure=DUB&arrival=WRO&departureDateTime=2023-11-12T23:01&arrivalDateTime=2023-12-13T23:01)
`localhost:8080/api/v1/interconnections?departure=DUB&arrival=WRO&departureDateTime=2023-11-12T23:01&arrivalDateTime=2023-12-13T23:01`

## Project Structure

The project structure follows standard Spring Boot application conventions. Key components include:

- `com.adrian.ryanair.flightinterconnector.controller`: Contains the main controller for handling API requests.

- `com.adrian.ryanair.flightinterconnector.service`: Service classes for fetching flight data.


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
