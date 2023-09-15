package com.adrian.ryanair.flightinterconnector.Controller;

import com.adrian.ryanair.flightinterconnector.controller.InterconnectionsController;
import com.adrian.ryanair.flightinterconnector.service.InterconnectionService;
import com.adrian.ryanair.flightinterconnector.service.RouteApiService;
import com.adrian.ryanair.flightinterconnector.service.SchedulesApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

@WebMvcTest(controllers = InterconnectionsController.class)
class InterconnectionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    SchedulesApiService schedulesApiService;
    @MockBean
    RouteApiService routeApiService;
    @MockBean
    InterconnectionService interconnectionService;

    @Test
    void whenValidRequest_then200Response() throws Exception {
        LocalDateTime departureDateTime = LocalDateTime.of(2023, 1, 1, 12, 2, 6);
        LocalDateTime arrivalDateTime = LocalDateTime.of(2023, 1, 2, 10, 34, 2);

       mockMvc.perform(MockMvcRequestBuilders
               .get("/api/v1/interconnections")
               .param("departure", "DUB")
               .param("arrival", "WRO")
               .param("departureDateTime", departureDateTime.toString())
               .param("arrivalDateTime", arrivalDateTime.toString())
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(MockMvcResultMatchers.status().isOk());

   }

    @Test
    void whenInvalidRequest_then400Response() throws Exception {
        LocalDateTime arrivalDateTime = LocalDateTime.of(2023, 1, 2, 10, 34, 2);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/interconnections")
                        .param("departure", "NCE")
                        .param("arrival", "MAD")
                        .param("departureDateTime", "2023-01-01T12:wrong")
                        .param("arrivalDateTime", arrivalDateTime.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());


    }

}
