package pednav.backend.pednav;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PedNavApplication {

    public static void main(String[] args) {
        SpringApplication.run(PedNavApplication.class, args);
    }

}
