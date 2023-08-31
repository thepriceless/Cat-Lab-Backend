package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class OwnerCatRelationshipException extends RuntimeException {
    public static OwnerCatRelationshipException CatIsOwnedByAnotherOwner(Long catId, Long ownerId) {
        return new OwnerCatRelationshipException(String.format("Cannot own cat because person with id = %d already owns the cat with id = %d", ownerId, catId));
    }

    public OwnerCatRelationshipException(String ex) {super(ex);}
}
