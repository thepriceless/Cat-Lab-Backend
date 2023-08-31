package advices;

import exceptions.RegistrationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class RegistrationAdvice {
    @ResponseBody
    @ExceptionHandler(RegistrationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String exceptionHandler(RegistrationException ex) {
        return ex.getMessage();
    }
}
