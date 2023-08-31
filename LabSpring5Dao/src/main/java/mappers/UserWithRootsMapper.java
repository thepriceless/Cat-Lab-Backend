package mappers;

import businessuser.AdminUser;
import businessuser.BasicUser;
import businessuser.UserRoots;
import dto.UserDto;
import entities.securitymodels.User;

public class UserWithRootsMapper {
    public static UserRoots jpaUserToBusinessUser(User user) {
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            return new AdminUser(user);
        }

        return new BasicUser(user);
    }

    public static UserRoots jpaUserToBusinessUser(UserDto dto) {
        if (dto.getRolesId().stream().anyMatch(role -> role.equals(1L))) {
            return new AdminUser(dto);
        }

        return new BasicUser(dto);
    }
}
