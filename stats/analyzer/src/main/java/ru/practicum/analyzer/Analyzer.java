package ru.practicum.analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.analyzer.service.SimilarityProcessor;
import ru.practicum.analyzer.service.UserActionProcessor;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Analyzer {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Analyzer.class, args);

        final UserActionProcessor userActionProcessor = context.getBean(UserActionProcessor.class);
        final SimilarityProcessor similarityProcessor = context.getBean(SimilarityProcessor.class);

        //  запускаем в отдельном потоке обработчик действий пользователей
        Thread userActionThread = new Thread(userActionProcessor);
        userActionThread.setName("InteractionThread");
        userActionThread.start();

        // В текущем потоке запускаем обработчик сходств событий
        similarityProcessor.run();
    }
}
