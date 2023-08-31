package services;


import businessuser.UserRoots;
import controllermodels.CatControllerModel;
import dto.CatDto;
import dto.OwnerDto;
import dto.UserDto;
import entities.Cat;
import entities.CatColor;
import entities.Owner;
import exceptions.AccessDeniedException;
import exceptions.CatNotFoundException;
import exceptions.CatsRelationshipException;
import exceptions.OwnerCatRelationshipException;
import jakarta.transaction.Transactional;
import mappers.CatColorMapper;
import mappers.CatMapper;
import mappers.OwnerMapper;
import mappers.UserWithRootsMapper;
import messaging.RabbitMessagingClient;
import messaging.RabbitMessagingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import dao.repositories.CatRepository;
import wrappers.request.*;

import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.util.StringUtils.capitalize;

@Component
@ComponentScan(basePackages = {"dao.repositories"})
@Transactional
public class CatService implements ICatService{
    private final CatRepository repository;
    private final RabbitMessagingClient messagingClient;

    @Autowired
    public CatService(CatRepository catRepository, RabbitMessagingClient client) {
        this.repository = catRepository;
        this.messagingClient = client;
    }

    public List<CatDto> getAllCats() {
        return repository.findAll()
            .stream().map(CatMapper::catAsDto).collect(Collectors.toList());
    }

    @Override
    public List<CatDto> getAllCatsByUser(@NonNull String username) {
        UserDto userDto = (UserDto) messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToUserServiceRoutingName,
            new UsernameWrapper(username),
            UserDto.class,
            "get-user");

        return repository.findByOwner_OwnerId(userDto.getId())
            .stream().map(CatMapper::catAsDto).collect(Collectors.toList());
    }

    @Override
    public List<CatDto> getAllCatsByColor(@NonNull String colorName, @NonNull String username) {
        UserDto userDto = (UserDto) messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToUserServiceRoutingName,
            new UsernameWrapper(username),
            UserDto.class,
            "get-user");

        CatColor color = CatColor.valueOf(capitalize(colorName));

        return repository.findByColorEqualsAndOwner_OwnerId(color, userDto.getId())
            .stream().map(CatMapper::catAsDto).collect(Collectors.toList());
    }

    @Override
    public List<CatDto> getFreeCats() {
        return repository.findByOwnerIsNull()
            .stream().map(CatMapper::catAsDto).collect(Collectors.toList());
    }

    @Override
    public CatDto getCatById(@NonNull Long id) throws CatNotFoundException {
        Cat target = repository.findById(id).orElseThrow(() -> CatNotFoundException.NoCatById(id));

        return CatMapper.catAsDto(target);
    }

    @Override
    public CatDto getCatByIdWithUser(@NonNull Long catId, @NonNull String username) throws CatNotFoundException, AccessDeniedException {
        Cat target = repository.findById(catId).orElseThrow(() -> CatNotFoundException.NoCatById(catId));
        UserDto userDto = (UserDto) messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToUserServiceRoutingName,
            new UsernameWrapper(username),
            UserDto.class,
            "get-user");

        final UserRoots userWithRoots = UserWithRootsMapper.jpaUserToBusinessUser(userDto);
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

        repository.save(newCat);

        return CatMapper.catAsDto(newCat);
    }

    @Override
    public CatDto saveCat(@NonNull CatDto dto) {
        List<Cat> friends = dto.getFriends().stream().map(friendId -> repository.findById(friendId).orElse(null)).toList();

        Cat newCat = CatMapper.catDtoAsCat(
            dto,
            OwnerMapper.ownerDtoAsOwner(messagingClient.sendAndReceive(
                RabbitMessagingConfig.outcomeToOwnerServiceRoutingName,
                new OwnerIdWrapper(dto.getOwnerId()),
                OwnerDto.class,
                "get-one-owner"),
                friends),
            friends);

        repository.save(newCat);

        return CatMapper.catAsDto(newCat);
    }

    public void makeFriends(@NonNull Long catId1, @NonNull Long catId2) throws CatNotFoundException {
        Cat cat1 = repository.findById(catId1).orElseThrow(() -> CatNotFoundException.NoCatById(catId1));
        Cat cat2 = repository.findById(catId2).orElseThrow(() -> CatNotFoundException.NoCatById(catId2));

        if (!cat1.hasFriend(cat2)) {
            cat1.addFriend(cat2);
        }

        if (!cat2.hasFriend(cat1)) {
            cat2.addFriend(cat1);
        }

        repository.save(cat1);
        repository.save(cat2);
    }

    @Override
    public void makeFriendsWithUser(@NonNull Long catId1, @NonNull Long catId2, @NonNull String username) throws CatNotFoundException {
        Cat cat1 = repository.findById(catId1).orElseThrow(() -> CatNotFoundException.NoCatById(catId1));
        Cat cat2 = repository.findById(catId2).orElseThrow(() -> CatNotFoundException.NoCatById(catId2));
        UserDto userDto = (UserDto) messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToUserServiceRoutingName,
            new UsernameWrapper(username),
            UserDto.class,
            "get-user");

        final UserRoots userWithRoots = UserWithRootsMapper.jpaUserToBusinessUser(userDto);
        if (!userWithRoots.canOperateWithCat(cat1) && !userWithRoots.canOperateWithCat(cat2)) {
            throw AccessDeniedException.NoneOfCatsIsYours();
        }

        if (!cat1.hasFriend(cat2)) {
            cat1.addFriend(cat2);
        }

        if (!cat2.hasFriend(cat1)) {
            cat2.addFriend(cat1);
        }

        repository.save(cat1);
        repository.save(cat2);
    }

    @Override
    public void deleteCat(@NonNull Long catId) throws CatNotFoundException, CatsRelationshipException {
        Cat target = repository.findById(catId).orElse(null);

        if (target == null) { return; }

        for (Cat friend : target.getFriends()) {
            friend.removeFriend(target);
            repository.save(friend);
        }

        Owner potentialOwner = target.getOwner();
        if (potentialOwner != null) {
            unwireCatFromOwner(catId, potentialOwner.getOwnerId());
        }

        repository.delete(target);
    }

    @Override
    public void unmakeFriendsWithUser(@NonNull Long catId1, @NonNull Long catId2, @NonNull String username) throws CatNotFoundException, CatsRelationshipException {
        UserDto userDto = (UserDto) messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToUserServiceRoutingName,
            new UsernameWrapper(username),
            UserDto.class,
            "get-user");

        UserRoots userWithRoots = UserWithRootsMapper.jpaUserToBusinessUser(userDto);
        if (!userWithRoots.canOperateWithCat(repository.findById(catId1).orElse(null)) &&
            !userWithRoots.canOperateWithCat(repository.findById(catId2).orElse(null))) {
            throw AccessDeniedException.NoneOfCatsIsYours();
        }

        unmakeFriends(catId1, catId2);
    }

    @Override
    public void wireCatToOwnerWithUser(@NonNull Long catId, @NonNull Long ownerId, @NonNull String username)
        throws CatNotFoundException, OwnerCatRelationshipException, AccessDeniedException {

        UserDto userDto = (UserDto) messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToUserServiceRoutingName,
            new UsernameWrapper(username),
            UserDto.class,
            "get-user");

        UserRoots userWithRoots = UserWithRootsMapper.jpaUserToBusinessUser(userDto);
        if (!userDto.getId().equals(ownerId) && !userWithRoots.hasAdminRoots()) {
            throw AccessDeniedException.UserOwnerMismatch();
        }

        Cat targetCat = repository.findById(catId).orElse(null);

        if (targetCat == null) {
            System.err.println("nullllllll----------");
        }

        OwnerDto targetOwnerDto = messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToOwnerServiceRoutingName,
            new OwnerIdWrapper(ownerId),
            OwnerDto.class,
            "get-one-owner");

        Owner targetOwner = OwnerMapper.ownerDtoAsOwner(
            targetOwnerDto,
            targetOwnerDto.getPets().stream().map(petId -> repository.findById(petId).orElse(null)).toList());

        if (targetCat.getOwner() != null && targetCat.getOwner() != targetOwner) {
            throw OwnerCatRelationshipException.CatIsOwnedByAnotherOwner(catId, targetCat.getOwner().getOwnerId());
        }

        if (!targetOwner.ownsCat(targetCat)) {
            targetOwner.addCat(targetCat);
        }

        if (targetCat.getOwner() != targetOwner) {
            targetCat.setOwner(targetOwner);
        }

        repository.save(targetCat);
        messagingClient.sendWithoutResponse(
            RabbitMessagingConfig.outcomeToOwnerServiceRoutingName,
            new OwnerWrapper(
                targetOwner.getOwnerId(),
                targetOwner.getName(),
                targetOwner.getBirthday(),
                targetOwner.getPets().stream().map(Cat::getCatId).toList()),
            "save-owner");
    }

    @Override
    public void unwireCatFromOwnerWithUser(@NonNull Long catId, @NonNull Long ownerId, @NonNull String username)
        throws CatNotFoundException, AccessDeniedException {

        UserDto userDto = (UserDto) messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToUserServiceRoutingName,
            new UsernameWrapper(username),
            UserDto.class,
            "get-user");

        UserRoots userWithRoots = UserWithRootsMapper.jpaUserToBusinessUser(userDto);
        if (!userDto.getId().equals(ownerId) && !userWithRoots.hasAdminRoots()) {
            throw AccessDeniedException.UserOwnerMismatch();
        }

        Cat targetCat = repository.findById(catId).orElse(null);
        OwnerDto targetOwnerDto = messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToOwnerServiceRoutingName,
            new OwnerIdWrapper(ownerId),
            OwnerDto.class,
            "get-one-owner");

        Owner targetOwner = OwnerMapper.ownerDtoAsOwner(
            targetOwnerDto,
            targetOwnerDto.getPets().stream().map(petId -> repository.findById(petId).orElse(null)).toList());

        if (targetOwner.ownsCat(targetCat)) {
            targetOwner.removeCat(targetCat);
        }

        if (targetCat.getOwner() == targetOwner) {
            targetCat.setOwner(null);
        }

        repository.save(targetCat);
        messagingClient.sendWithoutResponse(
            RabbitMessagingConfig.outcomeToOwnerServiceRoutingName,
            new OwnerWrapper(
                targetOwner.getOwnerId(),
                targetOwner.getName(),
                targetOwner.getBirthday(),
                targetOwner.getPets().stream().map(cat -> cat.getCatId()).toList()),
            "save-owner");
    }

    private void unmakeFriends(@NonNull Long catId1, @NonNull Long catId2) throws CatNotFoundException, CatsRelationshipException {
        Cat cat1 = repository.findById(catId1).orElse(null);
        Cat cat2 = repository.findById(catId2).orElse(null);

        if (cat1 == null || cat2 == null) {return;}

        if (cat1.hasFriend(cat2)) {
            cat1.removeFriend(cat2);
        }

        if (cat2.hasFriend(cat1)) {
            cat2.removeFriend(cat1);
        }

        repository.save(cat1);
        repository.save(cat2);
    }

    private void unwireCatFromOwner(@NonNull Long catId, @NonNull Long ownerId) throws CatNotFoundException {
        Cat targetCat = repository.findById(catId).orElse(null);
        OwnerDto targetOwnerDto = messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToOwnerServiceRoutingName,
            new OwnerIdWrapper(ownerId),
            OwnerDto.class,
            "get-one-owner");

        Owner targetOwner = OwnerMapper.ownerDtoAsOwner(
            targetOwnerDto,
            targetOwnerDto.getPets().stream().map(petId -> repository.findById(petId).orElse(null)).toList());

        if (targetOwner.ownsCat(targetCat)) {
            targetOwner.removeCat(targetCat);
        }

        if (targetCat.getOwner() == targetOwner) {
            targetCat.setOwner(null);
        }

        repository.save(targetCat);
        messagingClient.sendWithoutResponse(
            RabbitMessagingConfig.outcomeToOwnerServiceRoutingName,
            new OwnerWrapper(
                targetOwner.getOwnerId(),
                targetOwner.getName(),
                targetOwner.getBirthday(),
                targetOwner.getPets().stream().map(cat -> cat.getCatId()).toList()),
            "save-owner");
    }
}