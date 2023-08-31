package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CatNotFoundException extends RuntimeException {
    public static CatNotFoundException NoCatById(Long id) {
        return new CatNotFoundException("No cat found by Id = " + id);
    }

    public CatNotFoundException(String ex) {super(ex);}
}
