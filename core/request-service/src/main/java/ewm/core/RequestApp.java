package ewm.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableFeignClients(basePackages = {"ewm.core.client"})
public class RequestApp {
    public static void main(String[] args) {

        SpringApplication.run(RequestApp.class, args);
    }
}
