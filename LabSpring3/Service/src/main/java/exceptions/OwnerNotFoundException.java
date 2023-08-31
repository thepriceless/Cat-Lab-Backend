package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OwnerNotFoundException extends RuntimeException {
    public static OwnerNotFoundException NoOwnerById(Long id) {
        return new OwnerNotFoundException("No owner found by Id = " + id);
    }

    public OwnerNotFoundException(String ex) {super(ex);}
}
