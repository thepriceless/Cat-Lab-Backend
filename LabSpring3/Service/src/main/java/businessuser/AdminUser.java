package businessuser;

import entities.Cat;
import entities.securitymodels.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AdminUser implements UserRoots{
    private User user;
    @Override
    public boolean canOperateWithCat(Cat id) {
        return true;
    }

    @Override
    public boolean hasAdminRoots() {
        return true;
    }
}
