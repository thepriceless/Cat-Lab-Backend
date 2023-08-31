package dbinit;

import dao.repositories.CatRepository;
import entities.Cat;
import entities.CatColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDate;


@ComponentScan(basePackages = {"dao.repositories"})
@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(CatRepository repository) {

        return args -> {
            log.info("Preloading " + repository.save(new Cat("Zyw0o", LocalDate.of(2003,8,1), "Shiz", CatColor.Grey)));
            log.info("Preloading " + repository.save(new Cat("Romik", LocalDate.of(1993,1,12), "Kren", CatColor.White)));
            log.info("Preloading " + repository.save(new Cat("Kotenochek", LocalDate.of(1999,9,13), "Huli", CatColor.Yellow)));
        };

    }
}
