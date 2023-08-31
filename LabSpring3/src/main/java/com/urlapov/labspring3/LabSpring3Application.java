package com.urlapov.labspring3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(
    basePackages = {
        "controllers",
        "repositories",
        "dbinit",
        "config"
    }
)
@EntityScan(basePackages = {"entities"})
@EnableJpaRepositories(basePackages = {"repositories"})
public class LabSpring3Application {

    public static void main(String[] args) {
        SpringApplication.run(LabSpring3Application.class, args);
    }

}
