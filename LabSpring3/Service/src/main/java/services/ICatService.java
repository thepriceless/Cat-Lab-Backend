package services;

import controllermodels.CatControllerModel;
import dto.CatDto;
import exceptions.AccessDeniedException;
import exceptions.CatNotFoundException;
import org.springframework.lang.NonNull;

import java.util.List;

public interface ICatService {
    List<CatDto> getAllCatsByUser(@NonNull String username);

    List<CatDto> getAllCatsByColor(@NonNull String colorName, @NonNull String username);

    List<CatDto> getFreeCats();

    CatDto getCatByIdWithUser(@NonNull Long catId, @NonNull String username) throws CatNotFoundException, AccessDeniedException;

    CatDto addCat(@NonNull CatControllerModel catModel);

    void makeFriendsWithUser(@NonNull Long catId1, @NonNull Long catId2, @NonNull String username) throws CatNotFoundException;
}
