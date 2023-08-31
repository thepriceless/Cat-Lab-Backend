package services.serviceImplementations;

import controllermodels.UserControllerModel;
import entities.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import exceptions.UserAlreadyExistsException;
import entities.securitymodels.Role;
import entities.securitymodels.User;
import repositories.OwnerRepository;
import repositories.security.UserRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
@ComponentScan(basePackages = {"repositories.security"})
public class UserService implements UserDetailsService, services.IUserService {
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       OwnerRepository ownerRepository,
                       BCryptPasswordEncoder encoder) {

        this.userRepository = userRepository;
        this.ownerRepository = ownerRepository;
        this.bCryptPasswordEncoder = encoder;
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(new User());
    }

    @Override
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @Override
    public void saveUserwithLinkedOwner(UserControllerModel userModel) throws UserAlreadyExistsException {
        final User user = new User(userModel.getUsername(), userModel.getPassword());

        if (userExists(user.getUsername())) {
            throw UserAlreadyExistsException.UserWithUsernameAlreadyExists(user.getUsername());
        }

        user.setRoles(Collections.singleton(new Role(2L, "ROLE_USER")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        final Owner owner = new Owner(userModel.getUsername(), LocalDate.now());
        ownerRepository.save(owner);
        user.setId(owner.getOwnerId());
        userRepository.save(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private boolean userExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }
}
