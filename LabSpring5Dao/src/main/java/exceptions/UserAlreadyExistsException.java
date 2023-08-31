package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyExistsException extends RuntimeException{
    private UserAlreadyExistsException(String message) {super(message);}

    public static UserAlreadyExistsException UserWithUsernameAlreadyExists(String username) {
        return new UserAlreadyExistsException(String.format("User with username %s already exists", username));
    }
}
