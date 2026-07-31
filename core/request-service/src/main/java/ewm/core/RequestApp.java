package ewm.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableFeignClients(basePackages = {"ewm.core.client"})
@ComponentScan(value = {"ewm", "client"})
public class RequestApp {
    public static void main(String[] args) {

        SpringApplication.run(RequestApp.class, args);
    }
}
