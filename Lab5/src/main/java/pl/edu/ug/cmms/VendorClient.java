package pl.edu.ug.cmms;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class VendorClient {
    
    private final RestClient restClient;

    public VendorClient(RestClient.Builder builder, @Value("${cmms.vendor.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public Optional<MaintenancePlan> plan(Machine.MachineType machineType) {
        try {
            MaintenancePlan plan = restClient.get()
                .uri("/api/maintenance-plans/{machineType}", machineType.name())
                .retrieve()
                .body(MaintenancePlan.class);
            return Optional.ofNullable(plan);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }
}
