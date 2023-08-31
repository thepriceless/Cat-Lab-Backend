package businessuser;

import entities.Cat;

public interface UserRoots {
    boolean canOperateWithCat(Cat id);
    boolean hasAdminRoots();
}
