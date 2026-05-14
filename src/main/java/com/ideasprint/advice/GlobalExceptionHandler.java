package com.ideasprint.advice;
import com.ideasprint.dto.ErrorResponse;
import com.ideasprint.exception.BusinessException;
import com.ideasprint.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@PropertySource("classpath:error-message.properties")
public class GlobalExceptionHandler {

    @Autowired
    private Environment env;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        log.warn(env.getProperty("log.resource.not.found"), ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        log.warn(env.getProperty("log.business.violation"), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        log.warn(env.getProperty("log.auth.failed"), ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, env.getProperty("error.invalid.credentials"));
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(org.springframework.dao.DataIntegrityViolationException ex) {
        log.warn(env.getProperty("log.data.integrity"), ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, env.getProperty("error.database.constraint"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn(env.getProperty("log.validation.failed"), message);
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error(env.getProperty("log.unexpected.error"), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, env.getProperty("error.unexpected"));
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        var error = new ErrorResponse(message);
        return ResponseEntity.status(status).body(error);
    }
}




