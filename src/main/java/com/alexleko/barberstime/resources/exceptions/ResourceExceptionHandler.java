package com.alexleko.barberstime.resources.exceptions;

import com.alexleko.barberstime.services.exceptions.BusinessException;
import com.alexleko.barberstime.services.exceptions.DataIntegrityException;
import com.alexleko.barberstime.services.exceptions.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@ControllerAdvice
public class ResourceExceptionHandler {

    private static final String DATA_NOT_FOUND = "Data Not Found";
    private static final String DATA_INTEGRITY = "Data Integrity";
    private static final String VALIDATION_ERROR = "Request field With Validation Error";
    private static final String INSERT_DUPLICATE = "Attempt insert duplicate";
    private static final String REQUEST_NOT_ALLOWED = "Request Not Allowed";

    /**
     * Exception handling for data not found in request.
     */
    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<StandardError> objectNotFound(ObjectNotFoundException exc, HttpServletRequest request) {

        StandardError error = new StandardError(
                LocalDateTime.now(),
//                System.currentTimeMillis(),
                HttpStatus.NOT_FOUND.value(),
                DATA_NOT_FOUND,
                exc.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Exception handling for data Integrity in request.
     * When exist Category with linked Works.
     */
    @ExceptionHandler(DataIntegrityException.class)
    public ResponseEntity<StandardError> dataIntegrity(DataIntegrityException exc, HttpServletRequest request) {
        StandardError error = new StandardError(
                LocalDateTime.now(),
//                System.currentTimeMillis(),
                HttpStatus.BAD_REQUEST.value(),
                DATA_INTEGRITY,
                exc.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Exception handling for invalid field parameter.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> validation(MethodArgumentNotValidException exc, HttpServletRequest request) {
        ValidationError error = new ValidationError(
                LocalDateTime.now(),
//              System.currentTimeMillis(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                VALIDATION_ERROR,
                exc.getMessage(),
                request.getRequestURI()
        );

        for (FieldError field : exc.getBindingResult().getFieldErrors()) {
            error.addErrors(field.getField(), field.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<StandardError> insertDuplicate(BusinessException exc, HttpServletRequest request) {
        StandardError error = new StandardError(
                LocalDateTime.now(),
//              System.currentTimeMillis(),
                HttpStatus.BAD_REQUEST.value(),
                INSERT_DUPLICATE,
                exc.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<StandardError> methodNotSupported(HttpRequestMethodNotSupportedException exc, HttpServletRequest request) {
        StandardError error = new StandardError(
                LocalDateTime.now(),
//              System.currentTimeMillis(),
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                REQUEST_NOT_ALLOWED,
                exc.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

}
