package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException{
    public static AccessDeniedException NotYourCat() {
        return new AccessDeniedException("User is not allowed to change entities that he does not own");
    }

    public static AccessDeniedException NoneOfCatsIsYours() {
        return new AccessDeniedException("User is not allowed to change entities that he does not own");
    }

    public static AccessDeniedException UserOwnerMismatch() {
        return new AccessDeniedException("User is not allowed to change entities that he does not own");
    }

    public AccessDeniedException(String ex) {super(ex);}
}
