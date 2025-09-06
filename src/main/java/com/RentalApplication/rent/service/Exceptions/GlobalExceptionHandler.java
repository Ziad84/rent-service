package com.RentalApplication.rent.service.Exceptions;

import com.RentalApplication.rent.service.DTO.RegisterResponseDTO;
import com.RentalApplication.rent.service.DTO.UpdateUserDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Authentication Failed");
        response.put("message", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RegisterResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(RegisterResponseDTO.builder()
                .status(false)
                .message("Incorrect format")
                .errors(errors)
                .build());


    }


    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<RegisterResponseDTO> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.badRequest().body(RegisterResponseDTO.builder()
                .status(false)
                .message("Registration failed")
                .errors(Map.of("email", ex.getMessage()))
                .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<UpdateUserDTO> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {

        UpdateUserDTO error = new UpdateUserDTO(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<UpdateUserDTO> handleRuntime(
            RuntimeException ex, HttpServletRequest request) {

        UpdateUserDTO error = new UpdateUserDTO(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<UpdateUserDTO> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        UpdateUserDTO body = new UpdateUserDTO(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return  ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

}
