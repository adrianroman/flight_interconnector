package com.adrian.ryanair.flightinterconnector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
class FlightInterconnectorApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() {
		objectMapper = new ObjectMapper();
	}
	@Test
	void whenValidRequest_thenReturnCorrectFlights() throws Exception {

		LocalDateTime departureDateTime = LocalDateTime.of(2023, 11, 1, 10,
				34, 2);

		LocalDateTime arrivalDateTime = departureDateTime.plusMonths(1).plusDays(1);
		String formattedDepartureDate = TestUtils.formatDate(departureDateTime);
		String formattedArrivalDate = TestUtils.formatDate(arrivalDateTime);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
						.get("/api/v1/interconnections")
						.param("departure", "DUB")
						.param("arrival", "WRO")
						.param("departureDateTime", formattedDepartureDate)
						.param("arrivalDateTime", formattedArrivalDate)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		String contentAsString = result.getResponse().getContentAsString();
		assert(TestUtils.validateInterconnectedFlightsResponse(contentAsString,departureDateTime, arrivalDateTime));

	}
	@Test
	void whenInValidDateRequest_thenReturnError() throws Exception {

		LocalDateTime departureDateTime = LocalDateTime.of(2023, 11, 1, 10,
				34, 2);

		LocalDateTime arrivalDateTime = departureDateTime.minusMonths(6);
		String formattedDepartureDate = TestUtils.formatDate(departureDateTime);
		String formattedArrivalDate = TestUtils.formatDate(arrivalDateTime);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
						.get("/api/v1/interconnections")
						.param("departure", "DUB")
						.param("arrival", "WRO")
						.param("departureDateTime", formattedDepartureDate)
						.param("arrivalDateTime", formattedArrivalDate)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andReturn();

		JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());

		String error = responseBody.get("error").asText();
		String message = responseBody.get("message").asText();

		Assertions.assertEquals("Invalid dates", error);
		Assertions.assertEquals("Arrival date must be after departure date.", message);

	}


}
