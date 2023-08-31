package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
public class NotImplementedException extends RuntimeException{
    public static NotImplementedException NoImplementation() {
        return new NotImplementedException("No service method");
    }

    public NotImplementedException(String ex) {super(ex);}
}