package services.serviceImplementations;


import businessuser.UserRoots;
import controllermodels.CatControllerModel;
import dto.CatDto;
import entities.Cat;
import entities.CatColor;
import entities.securitymodels.User;
import exceptions.AccessDeniedException;
import exceptions.CatNotFoundException;
import mappers.CatMapper;
import mappers.UserWithRootsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import repositories.CatRepository;
import repositories.security.UserRepository;
import services.ICatService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.capitalize;


@Component
@ComponentScan(basePackages = {"repositories"})
public class CatService implements ICatService {
    private final CatRepository catRepository;

    private final UserRepository userRepository;

    @Autowired
    public CatService(CatRepository catRepository, UserRepository userRepository) {
        this.catRepository = catRepository;
        this.userRepository = userRepository;
    }

    public List<CatDto> getAllCats() {
        return catRepository.findAll()
            .stream().map(CatMapper::catAsDto).collect(Collectors.toList());
    }

    @Override
    public List<CatDto> getAllCatsByUser(@NonNull String username) {
        User user = userRepository.findByUsername(username);

        return catRepository.findByOwner_OwnerId(user.getId())
            .stream().map(CatMapper::catAsDto).collect(Collectors.toList());
    }

    @Override
    public List<CatDto> getAllCatsByColor(@NonNull String colorName, @NonNull String username) {
        User user = userRepository.findByUsername(username);
        CatColor color = CatColor.valueOf(capitalize(colorName));

        return catRepository.findByColorEqualsAndOwner_OwnerId(color, user.getId())
            .stream().map(CatMapper::catAsDto).collect(Collectors.toList());
    }

    @Override
    public List<CatDto> getFreeCats() {
        return catRepository.findByOwnerIsNull()
            .stream().map(CatMapper::catAsDto).collect(Collectors.toList());
    }

    public CatDto getCatById(@NonNull Long id) throws CatNotFoundException {
        Cat target = catRepository.findById(id).orElseThrow(() -> CatNotFoundException.NoCatById(id));

        return CatMapper.catAsDto(target);
    }

    @Override
    public CatDto getCatByIdWithUser(@NonNull Long catId, @NonNull String username) throws CatNotFoundException, AccessDeniedException {
        Cat target = catRepository.findById(catId).orElseThrow(() -> CatNotFoundException.NoCatById(catId));
        User user = userRepository.findByUsername(username);

        final UserRoots userWithRoots = UserWithRootsMapper.jpaUserToBusinessUser(user);
        if (!userWithRoots.canOperateWithCat(target)) {
            throw AccessDeniedException.NotYourCat();
        }

        return CatMapper.catAsDto(target);
    }

    @Override
    public CatDto addCat(@NonNull CatControllerModel catModel) {
        Cat newCat = new Cat(
            catModel.getName(),
            catModel.getBirthday(),
            catModel.getSpecies(),
            catModel.getColor());

        catRepository.save(newCat);

        return CatMapper.catAsDto(newCat);
    }

    public void makeFriends(@NonNull Long catId1, @NonNull Long catId2) throws CatNotFoundException {
        Cat cat1 = catRepository.findById(catId1).orElseThrow(() -> CatNotFoundException.NoCatById(catId1));
        Cat cat2 = catRepository.findById(catId2).orElseThrow(() -> CatNotFoundException.NoCatById(catId2));

        if (!cat1.hasFriend(cat2)) {
            cat1.addFriend(cat2);
        }

        if (!cat2.hasFriend(cat1)) {
            cat2.addFriend(cat1);
        }

        catRepository.save(cat1);
        catRepository.save(cat2);
    }

    @Override
    public void makeFriendsWithUser(@NonNull Long catId1, @NonNull Long catId2, @NonNull String username) throws CatNotFoundException {
        Cat cat1 = catRepository.findById(catId1).orElseThrow(() -> CatNotFoundException.NoCatById(catId1));
        Cat cat2 = catRepository.findById(catId2).orElseThrow(() -> CatNotFoundException.NoCatById(catId2));
        User user = userRepository.findByUsername(username);

        final UserRoots userWithRoots = UserWithRootsMapper.jpaUserToBusinessUser(user);
        if (!userWithRoots.canOperateWithCat(cat1) && !userWithRoots.canOperateWithCat(cat2)) {
            throw AccessDeniedException.NoneOfCatsIsYours();
        }

        if (!cat1.hasFriend(cat2)) {
            cat1.addFriend(cat2);
        }

        if (!cat2.hasFriend(cat1)) {
            cat2.addFriend(cat1);
        }

        catRepository.save(cat1);
        catRepository.save(cat2);
    }
}