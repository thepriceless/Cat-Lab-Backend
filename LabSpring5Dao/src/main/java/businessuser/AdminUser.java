package businessuser;

import dto.UserDto;
import entities.Cat;
import entities.securitymodels.User;
import lombok.AllArgsConstructor;
import mappers.UserMapper;

@AllArgsConstructor
public class AdminUser implements UserRoots{
    private UserDto user;

    public AdminUser(User user) {
        this.user = UserMapper.userAsDto(user);
    }

    @Override
    public boolean canOperateWithCat(Cat id) {
        return true;
    }

    @Override
    public boolean hasAdminRoots() {
        return true;
    }
}
