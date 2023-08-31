package services;

import controllermodels.OwnerControllerModel;
import dto.OwnerDto;
import exceptions.CatsRelationshipException;
import exceptions.OwnerNotFoundException;
import org.springframework.lang.NonNull;

import java.util.List;

public interface IOwnerService {
    List<OwnerDto> getAllOwners();

    OwnerDto addOwner(@NonNull OwnerControllerModel ownerModel);

    OwnerDto saveOwner(@NonNull OwnerDto dto);

    OwnerDto getOwnerDtoById(@NonNull Long id) throws OwnerNotFoundException;

    void deleteOwner(@NonNull Long ownerId) throws CatsRelationshipException;
}
