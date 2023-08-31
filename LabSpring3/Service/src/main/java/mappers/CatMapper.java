package mappers;

import dto.CatDto;
import entities.Cat;
import enums.CatColorMapper;


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
}
