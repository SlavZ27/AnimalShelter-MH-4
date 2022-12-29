package pro.sky.animalshelter4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnimalShelter4Application {

    public static void main(String[] args) {
        SpringApplication.run(AnimalShelter4Application.class, args);
    }

}
