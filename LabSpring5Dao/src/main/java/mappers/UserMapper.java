package mappers;

import dto.UserDto;
import entities.securitymodels.Role;
import entities.securitymodels.User;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDto userAsDto(User user) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            user.getRoles().stream().map(Role::getId).collect(Collectors.toSet()));
    }

    public static User userDtoAsUser(UserDto dto) {
        User result = new User(dto.getUsername(), dto.getPassword());
        result.setId(dto.getId());

        return result;
    }
}
