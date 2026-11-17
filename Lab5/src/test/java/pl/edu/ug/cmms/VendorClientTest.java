package pl.edu.ug.cmms;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class VendorClientTest {

    private RestClient.Builder restClientBuilder;
    private MockRestServiceServer mockServer;
    private VendorClient vendorClient;

    @BeforeEach
    void setUp() {
        restClientBuilder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        vendorClient = new VendorClient(restClientBuilder, "http://localhost:8090");
    }

    @Test
    @DisplayName("Dla odpowiedzi 200 zwraca plan z polami machineType, inspectionIntervalDays i preventiveIntervalDays")
    void shouldReturnMaintenancePlanOn200() {
        String jsonResponse = """
            {
                "machineType": "CNC_LATHE",
                "inspectionIntervalDays": 30,
                "preventiveIntervalDays": 90
            }
            """;

        mockServer.expect(requestTo("http://localhost:8090/api/maintenance-plans/CNC_LATHE"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Optional<MaintenancePlan> plan = vendorClient.plan(Machine.MachineType.CNC_LATHE);

        assertTrue(plan.isPresent());
        assertEquals("CNC_LATHE", plan.get().machineType());
        assertEquals(30, plan.get().inspectionIntervalDays());
        assertEquals(90, plan.get().preventiveIntervalDays());
        mockServer.verify();
    }

    @Test
    @DisplayName("Dla odpowiedzi 404 zwraca pusty Optional (brak typu w katalogu)")
    void shouldReturnEmptyOptionalOn404() {
        mockServer.expect(requestTo("http://localhost:8090/api/maintenance-plans/COMPRESSOR"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withResourceNotFound());

        Optional<MaintenancePlan> plan = vendorClient.plan(Machine.MachineType.COMPRESSOR);

        assertTrue(plan.isEmpty());
        mockServer.verify();
    }

    @Test
    @DisplayName("Dla odpowiedzi 500 rzuca błąd serwera (usługa niedostępna)")
    void shouldThrowHttpServerErrorExceptionOn500() {
        mockServer.expect(requestTo("http://localhost:8090/api/maintenance-plans/CONVEYOR"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        assertThrows(HttpServerErrorException.class, () -> vendorClient.plan(Machine.MachineType.CONVEYOR));
        mockServer.verify();
    }
}
