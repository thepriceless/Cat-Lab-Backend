package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RegistrationException extends Exception{
    private RegistrationException(String message) {super(message);}

    public static RegistrationException PasswordDontMatch() {
        return new RegistrationException("Passwords should match");
    }
}