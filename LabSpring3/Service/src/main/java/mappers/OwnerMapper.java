package mappers;

import dto.OwnerDto;
import entities.Cat;
import entities.Owner;

public class OwnerMapper {
    public static OwnerDto ownerAsDto(Owner owner){
        return new OwnerDto(
            owner.getOwnerId(),
            owner.getName(),
            owner.getBirthday(),
            owner.getPets().stream().map(Cat::getCatId).toList());
    }
}
