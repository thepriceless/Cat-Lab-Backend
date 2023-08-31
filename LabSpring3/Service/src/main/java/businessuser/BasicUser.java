package businessuser;


import entities.Cat;
import entities.securitymodels.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BasicUser implements UserRoots {
    private User user;
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
