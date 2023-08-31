package advices;

import exceptions.CatNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestControllerAdvice
public class CatNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(CatNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String exceptionHandler(CatNotFoundException ex) {
        return ex.getMessage();
    }
}
