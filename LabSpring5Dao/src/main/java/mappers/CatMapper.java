package mappers;

import dto.CatDto;
import entities.Cat;
import entities.Owner;

import java.util.ArrayList;
import java.util.List;


public class CatMapper {
    public static CatDto catAsDto(Cat cat){
        return new CatDto(
            cat.getCatId(),
            cat.getName(),
            cat.getBirthday(),
            cat.getSpecies(),
            CatColorMapper.CatColorToServiceCatColor(cat.getColor()),
            cat.getOwner() != null ? cat.getOwner().getOwnerId() : null,
            cat.getFriends().stream().map(c -> c.getCatId()).toList());
    }

    public static Cat catDtoAsCat(CatDto dto, Owner owner, List<Cat> friends) {
        return new Cat(
            dto.getCatId(),
            dto.getName(),
            dto.getBirthday(),
            dto.getSpecies(),
            CatColorMapper.ServiceCatColorToCatColor(dto.getColor()),
            owner,
            friends);
    }

    public static Cat catDtoAsCatWithMockFriends(CatDto dto, Owner owner) {
        return new Cat(
            dto.getCatId(),
            dto.getName(),
            dto.getBirthday(),
            dto.getSpecies(),
            CatColorMapper.ServiceCatColorToCatColor(dto.getColor()),
            owner,
            new ArrayList<>());
    }
}
