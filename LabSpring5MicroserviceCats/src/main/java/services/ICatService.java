package services;

import controllermodels.CatControllerModel;
import dto.CatDto;
import exceptions.AccessDeniedException;
import exceptions.CatNotFoundException;
import exceptions.CatsRelationshipException;
import exceptions.OwnerCatRelationshipException;
import org.springframework.lang.NonNull;

import java.util.List;

public interface ICatService {
    List<CatDto> getAllCatsByUser(@NonNull String username);

    List<CatDto> getAllCatsByColor(@NonNull String colorName, @NonNull String username);

    List<CatDto> getFreeCats();

    CatDto getCatById(@NonNull Long id) throws CatNotFoundException;

    CatDto getCatByIdWithUser(@NonNull Long catId, @NonNull String username) throws CatNotFoundException, AccessDeniedException;

    CatDto addCat(@NonNull CatControllerModel catModel);

    CatDto saveCat(@NonNull CatDto dto);

    void makeFriendsWithUser(@NonNull Long catId1, @NonNull Long catId2, @NonNull String username) throws CatNotFoundException;

    void deleteCat(@NonNull Long catId) throws CatNotFoundException, CatsRelationshipException;

    void unmakeFriendsWithUser(@NonNull Long catId1, @NonNull Long catId2, @NonNull String username) throws CatNotFoundException, CatsRelationshipException;

    void wireCatToOwnerWithUser(@NonNull Long catId, @NonNull Long ownerId, @NonNull String username)
        throws CatNotFoundException, OwnerCatRelationshipException, AccessDeniedException;

    void unwireCatFromOwnerWithUser(@NonNull Long catId, @NonNull Long ownerId, @NonNull String username)
        throws CatNotFoundException, AccessDeniedException;
}
