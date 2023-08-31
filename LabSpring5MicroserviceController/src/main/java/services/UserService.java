package services;

import controllermodels.UserControllerModel;
import dao.repositories.RoleRepository;
import dao.repositories.UserRepository;
import dto.OwnerDto;
import dto.UserDto;
import entities.Owner;
import entities.securitymodels.Role;
import entities.securitymodels.User;
import exceptions.UserAlreadyExistsException;
import mappers.UserMapper;
import messaging.RabbitMessagingClient;
import messaging.RabbitMessagingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import wrappers.request.OwnerControllerModelWrapper;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@ComponentScan(basePackages = {"dao.repositories", "entities"})
public class UserService implements UserDetailsService, IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RabbitMessagingClient messagingClient;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder encoder,
                       RabbitMessagingClient client) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = encoder;
        this.messagingClient = client;
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(new User());
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDto saveUserWithLinkedOwner(UserControllerModel userModel) throws UserAlreadyExistsException {
        final User user = new User(userModel.getUsername(), userModel.getPassword());

        if (userExists(user.getUsername())) {
            throw UserAlreadyExistsException.UserWithUsernameAlreadyExists(user.getUsername());
        }

        final Owner owner = new Owner(userModel.getUsername(), LocalDate.now());

        /*OwnerDto ownerDto = (OwnerDto) messagingClient.sendAndReceiveTest(
            RabbitMessagingConfig.outcomeToOwnersRoutingName,
            new OwnerWrapper(owner.getName(), owner.getBirthday()),
            // OwnerDto.class,
            "create-owner");*/

        OwnerDto ownerDto = (OwnerDto) messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToOwnersRoutingName,
            new OwnerControllerModelWrapper(owner.getName(), owner.getBirthday()),
            OwnerDto.class,
            "create-owner");

        user.setRoles(Collections.singleton(new Role(2L, "ROLE_USER")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setId(ownerDto.getOwnerId());

        userRepository.save(user);

        return UserMapper.userAsDto(user);
    }

    public Set<Role> getRolesByIds(Set<Long> ids) {
        return ids.stream().map(id -> roleRepository.findById(id).orElse(null)).collect(Collectors.toSet());
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
