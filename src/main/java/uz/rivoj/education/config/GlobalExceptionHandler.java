package uz.rivoj.education.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uz.rivoj.education.dto.error.ErrorDTO;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.exception.NotEnoughFundsException;
import uz.rivoj.education.exception.WrongPasswordException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorDTO> dataNotFoundExceptionHandler(DataNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO(e.getMessage(), 404));
    }
    @ExceptionHandler(DataAlreadyExistsException.class)
    public ResponseEntity<ErrorDTO> handleDataAlreadyExistsException(DataAlreadyExistsException  e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO(e.getMessage(), 409));
    }
    @ExceptionHandler(NotEnoughFundsException.class)
    public ResponseEntity<ErrorDTO> notEnoughFundsExceptionHandler(NotEnoughFundsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDTO(e.getMessage(), 401));
    }
    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ErrorDTO> wrongPasswordException(WrongPasswordException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO(e.getMessage(), 400));
    }
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorDTO> bindExceptionHandler(BindException e) {
        e.getAllErrors().get(0).getDefaultMessage();

        StringBuilder errors = new StringBuilder();
        e.getAllErrors().forEach(error -> {
            errors.append(error.getDefaultMessage()).append("\n");
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO(errors.toString(), 400));
    }
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.warn("JSON parse error: {}", ex.getMessage());
        String errorMessage = "JSON formatida xatolik: " + ex.getLocalizedMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        String errorMessage = "Serverda xatolik yuz berdi: " + ex.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }
}
