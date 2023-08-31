package services.serviceImplementations;

import businessuser.UserRoots;
import entities.Cat;
import entities.Owner;
import entities.securitymodels.User;
import exceptions.*;
import mappers.UserWithRootsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import repositories.CatRepository;
import repositories.OwnerRepository;
import repositories.security.UserRepository;
import services.IConnectingService;

@Component
@ComponentScan(basePackages = {"repositories"})
public class ConnectingService implements IConnectingService {
    private final CatRepository catRepository;
    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;

    @Autowired
    public ConnectingService(CatRepository catRepository, OwnerRepository ownerRepository, UserRepository userRepository) {
        this.catRepository = catRepository;
        this.ownerRepository = ownerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void wireCatToOwnerWithUser(@NonNull Long catId, @NonNull Long ownerId, @NonNull String username)
        throws CatNotFoundException, OwnerCatRelationshipException, AccessDeniedException {
        User user = userRepository.findByUsername(username);

        UserRoots userWithRoots = UserWithRootsMapper.jpaUserToBusinessUser(user);
        if (!user.getId().equals(ownerId) && !userWithRoots.hasAdminRoots()) {
            throw AccessDeniedException.UserOwnerMismatch();
        }

        wireCatToOwner(catId, ownerId);
    }

    @Override
    public void unwireCatFromOwnerWithUser(@NonNull Long catId, @NonNull Long ownerId, @NonNull String username)
        throws CatNotFoundException, AccessDeniedException {
        User user = userRepository.findByUsername(username);

        UserRoots userWithRoots = UserWithRootsMapper.jpaUserToBusinessUser(user);
        if (!user.getId().equals(ownerId) && !userWithRoots.hasAdminRoots()) {
            throw AccessDeniedException.UserOwnerMismatch();
        }

        unwireCatFromOwner(catId, ownerId);
    }

    @Override
    public void deleteCat(@NonNull Long catId) throws CatNotFoundException, CatsRelationshipException {
        Cat target = catRepository.findById(catId).orElse(null);
        if (target == null) { return; }

        for (Cat friend : target.getFriends()) {
            friend.removeFriend(target);
            catRepository.save(friend);
        }

        Owner potentialOwner = target.getOwner();
        if (potentialOwner != null) {
            unwireCatFromOwner(catId, potentialOwner.getOwnerId());
        }

        catRepository.delete(target);
    }

    @Override
    public void deleteOwner(@NonNull Long ownerId) throws CatsRelationshipException {
        Owner target = ownerRepository.findById(ownerId).orElse(null);
        if (target == null) { return; }

        for (Cat cat : target.getPets()) {
            cat.setOwner(null);
            catRepository.save(cat);
        }

        ownerRepository.delete(target);
    }

    @Override
    public void unmakeFriendsWithUser(@NonNull Long catId1, @NonNull Long catId2, @NonNull String username) throws CatNotFoundException, CatsRelationshipException {
        User user = userRepository.findByUsername(username);

        UserRoots userWithRoots = UserWithRootsMapper.jpaUserToBusinessUser(user);
        if (!userWithRoots.canOperateWithCat(getCatById(catId1)) && !userWithRoots.canOperateWithCat(getCatById(catId2))) {
            throw AccessDeniedException.NoneOfCatsIsYours();
        }

        unmakeFriends(catId1, catId2);
    }

    private Cat getCatById(@NonNull Long id) {
        return catRepository.findById(id).orElseThrow(() -> CatNotFoundException.NoCatById(id));
    }

    private Owner getOwnerById(@NonNull Long id) {
        return ownerRepository.findById(id).orElseThrow(() -> OwnerNotFoundException.NoOwnerById(id));
    }

    private void wireCatToOwner(@NonNull Long catId, @NonNull Long ownerId) throws CatNotFoundException, OwnerCatRelationshipException {
        Cat targetCat = getCatById(catId);
        Owner targetOwner = getOwnerById(ownerId);

        if (targetCat.getOwner() != null && targetCat.getOwner() != targetOwner) {
            throw OwnerCatRelationshipException.CatIsOwnedByAnotherOwner(catId, targetCat.getOwner().getOwnerId());
        }

        if (!targetOwner.ownsCat(targetCat)) {
            targetOwner.addCat(targetCat);
        }

        if (targetCat.getOwner() != targetOwner) {
            targetCat.setOwner(targetOwner);
        }

        catRepository.save(targetCat);
        ownerRepository.save(targetOwner);
    }

    private void unwireCatFromOwner(@NonNull Long catId, @NonNull Long ownerId) throws CatNotFoundException {
        Cat targetCat = getCatById(catId);
        Owner targetOwner = getOwnerById(ownerId);

        if (targetOwner.ownsCat(targetCat)) {
            targetOwner.removeCat(targetCat);
        }

        if (targetCat.getOwner() == targetOwner) {
            targetCat.setOwner(null);
        }

        catRepository.save(targetCat);
        ownerRepository.save(targetOwner);
    }

    private void unmakeFriends(@NonNull Long catId1, @NonNull Long catId2) throws CatNotFoundException, CatsRelationshipException {
        Cat cat1 = getCatById(catId1);
        Cat cat2 = getCatById(catId2);

        if (cat1.hasFriend(cat2)) {
            cat1.removeFriend(cat2);
        }

        if (cat2.hasFriend(cat1)) {
            cat2.removeFriend(cat1);
        }

        catRepository.save(cat1);
        catRepository.save(cat2);
    }
}
