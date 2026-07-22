package ewm.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableFeignClients("ewm.core.client")
public class UserApp {
    public static void main(String[] args) {

        SpringApplication.run(UserApp.class, args);
    }
}
