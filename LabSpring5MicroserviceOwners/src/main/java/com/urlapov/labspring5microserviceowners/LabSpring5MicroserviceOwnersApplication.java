package com.urlapov.labspring5microserviceowners;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({
    "messaging"
})
@EntityScan(basePackages = {"entities"})
@EnableJpaRepositories(basePackages = {"dao.repositories"})
public class LabSpring5MicroserviceOwnersApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabSpring5MicroserviceOwnersApplication.class, args);
    }

}
