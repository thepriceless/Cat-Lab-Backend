package advices;

import exceptions.CatsRelationshipException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CatsRelationshipAdvice {
    @ResponseBody
    @ExceptionHandler(CatsRelationshipException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    String exceptionHandler(CatsRelationshipException ex) {
        return ex.getMessage();
    }
}
