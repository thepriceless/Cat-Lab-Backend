package com.urlapov.labspring5microservicecats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(
    basePackages = {
        "messaging",
        "dbinit"
    }
)
@EntityScan(basePackages = {"entities"})
@EnableJpaRepositories(basePackages = {"dao.repositories"})
public class LabSpring5MicroserviceCatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabSpring5MicroserviceCatsApplication.class, args);
    }

}
