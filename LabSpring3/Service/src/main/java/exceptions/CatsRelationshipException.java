package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class CatsRelationshipException extends RuntimeException {
    public CatsRelationshipException(String ex) {super(ex);}
}
