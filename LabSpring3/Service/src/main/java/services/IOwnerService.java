package services;

import controllermodels.OwnerControllerModel;
import dto.OwnerDto;
import entities.Owner;
import exceptions.OwnerNotFoundException;
import org.springframework.lang.NonNull;

import java.util.List;

public interface IOwnerService {
    List<OwnerDto> getAllOwners();

    OwnerDto addOwner(@NonNull OwnerControllerModel newOwner);

    OwnerDto getOwnerById(@NonNull Long id) throws OwnerNotFoundException;
}
