package services;


import businessuser.UserRoots;
import controllermodels.OwnerControllerModel;
import dto.CatDto;
import dto.OwnerDto;
import dto.RoleDto;
import dto.UserDto;
import entities.Cat;
import entities.Owner;
import entities.securitymodels.User;
import exceptions.*;
import jakarta.transaction.Transactional;
import mappers.*;
import messaging.RabbitMessagingClient;
import messaging.RabbitMessagingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;
import dao.repositories.OwnerRepository;
import wrappers.request.CatIdWrapper;
import wrappers.request.CatWrapper;

@Component
@ComponentScan(basePackages = {"dao.repositories"})
@Transactional
public class OwnerService implements IOwnerService {
    private final OwnerRepository repository;
    private final RabbitMessagingClient messagingClient;

    @Autowired
    public OwnerService(OwnerRepository repository, RabbitMessagingClient client) {
        this.repository = repository;
        this.messagingClient = client;
    }

    @Override
    public List<OwnerDto> getAllOwners() {
        return repository.findAll().stream().map(OwnerMapper::ownerAsDto).collect(Collectors.toList());
    }

    @Override
    public OwnerDto addOwner(@NonNull OwnerControllerModel ownerModel) {
        Owner newOwner = new Owner(ownerModel.getName(), ownerModel.getBirthday());

        repository.save(newOwner);

        return OwnerMapper.ownerAsDto(newOwner);
    }

    @Override
    public OwnerDto saveOwner(@NonNull OwnerDto dto) {
        Owner newOwner = OwnerMapper.ownerDtoAsOwner(
            dto,
            dto.getPets().stream().map(petId -> {
                CatDto petDto = messagingClient.sendAndReceive(
                    RabbitMessagingConfig.outcomeToCatServiceRoutingName,
                    new CatIdWrapper(petId),
                    CatDto.class,
                    "get-cat-by-id-mock");

                return CatMapper.catDtoAsCatWithMockFriends(petDto, OwnerMapper.ownerDtoAsOwnerMockingPets(dto));
            }).toList());

        repository.save(newOwner);

        return OwnerMapper.ownerAsDto(newOwner);
    }

    @Override
    public OwnerDto getOwnerDtoById(@NonNull Long id) throws OwnerNotFoundException {
        Owner target = repository.findById(id).orElseThrow(() -> OwnerNotFoundException.NoOwnerById(id));

        return OwnerMapper.ownerAsDto(target);
    }

    @Override
    public void deleteOwner(@NonNull Long ownerId) throws CatsRelationshipException {
        Owner target = repository.findById(ownerId).orElse(null);
        if (target == null) { return; }

        for (Cat cat : target.getPets()) {
            cat.setOwner(null);

            messagingClient.sendWithoutResponse(
                RabbitMessagingConfig.outcomeToCatServiceRoutingName,
                new CatWrapper(
                    cat.getCatId(),
                    cat.getName(),
                    cat.getBirthday(),
                    cat.getSpecies(),
                    CatColorMapper.CatColorToServiceCatColor(cat.getColor()),
                    cat.getOwner() != null ? cat.getOwner().getOwnerId() : null,
                    cat.getFriends().stream().map(c -> c.getCatId()).toList()),
                "save-cat");
        }

        repository.delete(target);
    }
}
