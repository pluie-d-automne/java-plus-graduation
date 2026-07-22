package ewm.core;

import ewm.core.client.FeignErrorDecoder;
import feign.Feign;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableFeignClients({"client", "ewm.core.client"})
public class EventApp {
    public static void main(String[] args) {
        SpringApplication.run(EventApp.class, args);
    }

    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder().errorDecoder(new FeignErrorDecoder());
    }
}