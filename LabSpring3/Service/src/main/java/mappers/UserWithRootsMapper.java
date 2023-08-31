package mappers;

import businessuser.AdminUser;
import businessuser.BasicUser;
import businessuser.UserRoots;
import entities.securitymodels.Role;
import entities.securitymodels.User;

public class UserWithRootsMapper {
    public static UserRoots jpaUserToBusinessUser(User user) {
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            return new AdminUser(user);
        }

        return new BasicUser(user);
    }
}
