package dbinit;

import dao.repositories.RoleRepository;
import dao.repositories.UserRepository;
import entities.Owner;
import entities.securitymodels.Role;
import entities.securitymodels.User;
import messaging.RabbitMessagingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

@ComponentScan(basePackages = {"dao.repositories", "config"})
@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final RabbitMessagingClient client;

    @Autowired
    public LoadDatabase(BCryptPasswordEncoder encoder, RabbitMessagingClient client) {
        this.bCryptPasswordEncoder = encoder;
        this.client = client;
    }

    @Bean
    CommandLineRunner initDatabase(RoleRepository roleRepository, UserRepository userRepository) {

        return args -> {
            Role roleAdmin = new Role(1L, "ROLE_ADMIN");
            Role roleUser = new Role(2L, "ROLE_USER");
            log.info("Preloading " + roleRepository.save(roleAdmin));
            log.info("Preloading " + roleRepository.save(roleUser));
            User admin = new User("admin", "fake");
            admin.setPassword(bCryptPasswordEncoder.encode("adminpassword"));
            admin.setRoles(new HashSet<Role>(Arrays.asList(roleAdmin, roleUser)));
            admin.setId(0L);
            log.info("Preloading " + userRepository.save(admin));
            User testUser = new User("test", "fake");
            testUser.setPassword(bCryptPasswordEncoder.encode("testpass"));
            testUser.setRoles(new HashSet<Role>(Arrays.asList(roleUser)));
            Owner testOwner = new Owner("test", LocalDate.now());

            // client.sendAndReceive(RabbitMessagingConfig.routingOwnerService, new OwnerWrapper(testOwner), Object.class, "create-owner");
            // log.info("Preloading " + ownerRepository.save(testOwner));
            testUser.setId(200L);
            log.info("Preloading " + userRepository.save(testUser));
        };

    }
}
