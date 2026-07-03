package com.juliandonati.backendPortafolio.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request){
        Map<String, Object> responseBody = new HashMap<>();

        responseBody.put("status", HttpStatus.NOT_FOUND);
        responseBody.put("error","Resource Not Found!");
        responseBody.put("message", ex.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex, WebRequest request){
        Map<String, Object> responseBody = new HashMap<>();

        responseBody.put("status", HttpStatus.BAD_REQUEST);
        responseBody.put("error", "Errores de validación");
        responseBody.put("message", ex.getMessage());

        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach((error) -> errores.put(error.getField(), error.getDefaultMessage()));

        responseBody.put("errores", errores);

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityException(DataIntegrityViolationException ex, WebRequest request){
        Map<String, Object> responseBody = new HashMap<>();

        responseBody.put("status", HttpStatus.CONFLICT);
        responseBody.put("error", "Error de conflicto de datos, asegúrate de que los datos ingresados sean únicos y de que los valores referenciados existan.");

        return new ResponseEntity<>(responseBody, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleInternalServerException(Exception ex, WebRequest request){
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        responseBody.put("error", "Ha ocurrido un error del lado interno del servidor.");

        System.err.println("Ocurrió un error inesperado: " + ex.getMessage());
        ex.printStackTrace();

        return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DuplicatedAttributeException.class)
    public ResponseEntity<Object> handleDuplicatedAttributeException(DuplicatedAttributeException ex, WebRequest request){
        Map<String, Object> responseBody = new HashMap<>();

        responseBody.put("status", HttpStatus.CONFLICT);
        responseBody.put("error","Conflicto: se intentó crear un atributo duplicado");
        responseBody.put("message", ex.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, WebRequest request){
        Map<String, Object> responseBody = new HashMap<>();

        responseBody.put("status", HttpStatus.PAYLOAD_TOO_LARGE);
        responseBody.put("error","La imagen que subiste excede los 15MB!");
        responseBody.put("message", ex.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request){
        Map <String, Object> responseBody = new HashMap<>();

        responseBody.put("status", HttpStatus.UNAUTHORIZED);
        responseBody.put("error","Token de acceso expirado. Vuelva a iniciar sesión.");
        responseBody.put("message", ex.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
    }
}
