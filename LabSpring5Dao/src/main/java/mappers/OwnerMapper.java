package mappers;

import controllermodels.OwnerControllerModel;
import dto.OwnerDto;
import entities.Owner;
import entities.Cat;
import wrappers.request.OwnerControllerModelWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OwnerMapper {
    public static OwnerDto ownerAsDto(Owner owner){
        return new OwnerDto(
            owner.getOwnerId(),
            owner.getName(),
            owner.getBirthday(),
            owner.getPets().stream().map(Cat::getCatId).toList());
    }

    public static Owner ownerDtoAsOwner(OwnerDto dto, List<Cat> pets){
        return new Owner(
            dto.getOwnerId(),
            dto.getName(),
            dto.getBirthday(),
            pets);
    }

    public static Owner ownerDtoAsOwnerMockingPets(OwnerDto dto){
        return new Owner(
            dto.getOwnerId(),
            dto.getName(),
            dto.getBirthday(),
            new ArrayList<>());
    }

    public static OwnerControllerModel ownerWrapperAsModel(OwnerControllerModelWrapper wrapper) {
        return new OwnerControllerModel(
            wrapper.getName(),
            wrapper.getBirthday());
    }
}
