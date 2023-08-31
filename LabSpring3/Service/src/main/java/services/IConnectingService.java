package services;

import exceptions.AccessDeniedException;
import exceptions.CatNotFoundException;
import exceptions.CatsRelationshipException;
import exceptions.OwnerCatRelationshipException;
import org.springframework.lang.NonNull;

public interface IConnectingService {
    void wireCatToOwnerWithUser(@NonNull Long catId, @NonNull Long ownerId, @NonNull String username)
        throws CatNotFoundException, OwnerCatRelationshipException, AccessDeniedException;

    void unwireCatFromOwnerWithUser(@NonNull Long catId, @NonNull Long ownerId, @NonNull String username)
        throws CatNotFoundException, AccessDeniedException;

    void deleteCat(@NonNull Long catId) throws CatNotFoundException, CatsRelationshipException;

    void deleteOwner(@NonNull Long ownerId) throws CatsRelationshipException;

    void unmakeFriendsWithUser(@NonNull Long catId1, @NonNull Long catId2, @NonNull String username) throws CatNotFoundException, CatsRelationshipException;
}
