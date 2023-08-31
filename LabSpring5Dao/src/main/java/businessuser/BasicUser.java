package businessuser;


import dto.UserDto;
import entities.Cat;
import entities.securitymodels.User;
import lombok.AllArgsConstructor;
import mappers.UserMapper;

@AllArgsConstructor
public class BasicUser implements UserRoots {
    private UserDto user;

    public BasicUser(User user) {
        this.user = UserMapper.userAsDto(user);
    }
    @Override
    public boolean canOperateWithCat(Cat cat) {
        if (cat.getOwner() == null) {
            return false;
        }
        return cat.getOwner().getOwnerId().equals(user.getId());
    }

    @Override
    public boolean hasAdminRoots() {
        return false;
    }
}
