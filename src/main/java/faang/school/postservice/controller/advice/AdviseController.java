package faang.school.postservice.controller.advice;

import faang.school.postservice.controller.LikeController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {LikeController.class})
public class AdviseController {

    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError validationNotPassedHandler(RuntimeException ex) {
        return new ResponseError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
    }
}