package uz.shaftoli.education.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uz.shaftoli.education.exception.DataNotFoundException;
import uz.shaftoli.education.exception.NotEnoughFundsException;
import uz.shaftoli.education.exception.WrongPasswordException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorDto> dataNotFoundExceptionHandler(DataNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(e.getMessage(), 404));

    }
    @ExceptionHandler(NotEnoughFundsException.class)
    public ResponseEntity<ErrorDto> notEnoughFundsExceptionHandler(NotEnoughFundsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDto(e.getMessage(), 401));
    }
    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ErrorDto> wrongPasswordException(WrongPasswordException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto(e.getMessage(), 400));
    }
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorDto> bindExceptionHandler(BindException e) {
        e.getAllErrors().get(0).getDefaultMessage();

        StringBuilder errors = new StringBuilder();
        e.getAllErrors().forEach(error -> {
            errors.append(error.getDefaultMessage()).append("\n");
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto(errors.toString(), 400));
    }
}
