package pl.edu.ug.cmms;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration 
public class CmmsConfig {
    
    @Bean
    public List<Machine> machines() {
        return SampleData.machines();
    }

    @Bean
    public List<WorkOrder> workOrders() {
        return SampleData.workOrders();
    }

    @Bean
    @Profile("demo")
    public Clock demoClock() {
        LocalDateTime demoTime = LocalDateTime.parse("2026-09-21T06:00:00");
        return Clock.fixed(demoTime.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
    }

    @Bean
    @Profile("!demo")
    public Clock systemClock() {
        return Clock.systemDefaultZone();
    }
}
