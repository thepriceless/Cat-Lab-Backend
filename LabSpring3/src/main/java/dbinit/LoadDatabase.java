package dbinit;

import entities.Cat;
import entities.CatColor;
import entities.Owner;
import entities.securitymodels.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import repositories.CatRepository;
import repositories.OwnerRepository;
import entities.securitymodels.Role;
import repositories.security.RoleRepository;
import repositories.security.UserRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

@ComponentScan(basePackages = {"repositories"})
@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public LoadDatabase(BCryptPasswordEncoder encoder) {
        this.bCryptPasswordEncoder = encoder;
    }

    @Bean
    CommandLineRunner initDatabase(CatRepository catRepository, OwnerRepository ownerRepository, RoleRepository roleRepository, UserRepository userRepository) {

        return args -> {
            log.info("Preloading " + catRepository.save(new Cat("Zyw0o", LocalDate.of(2003,8,1), "Shiz", CatColor.Grey)));
            log.info("Preloading " + catRepository.save(new Cat("Romik", LocalDate.of(1993,1,12), "Kren", CatColor.White)));
            log.info("Preloading " + catRepository.save(new Cat("Kotenochek", LocalDate.of(1999,9,13), "Huli", CatColor.Yellow)));
            // log.info("Preloading " + ownerRepository.save(new Owner("Evgeniy Pesochin", LocalDate.of(2002,10,29))));
            // log.info("Preloading " + ownerRepository.save(new Owner("Maria Moskalenko", LocalDate.of(1933,4,1))));
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
            log.info("Preloading " + ownerRepository.save(testOwner));
            testUser.setId(testOwner.getOwnerId());
            log.info("Preloading " + userRepository.save(testUser));
        };

    }
}
