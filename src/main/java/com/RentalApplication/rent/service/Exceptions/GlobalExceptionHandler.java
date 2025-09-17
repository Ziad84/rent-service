package com.RentalApplication.rent.service.Exceptions;

import com.RentalApplication.rent.service.DTO.RegisterResponseDTO;
import com.RentalApplication.rent.service.DTO.UpdateUserResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
                .message("Incorrect format")
                .errors(errors)
                .build());


    }


    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<RegisterResponseDTO> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.badRequest().body(RegisterResponseDTO.builder()
                .message("Registration failed")
                .errors(Map.of("email", ex.getMessage()))
                .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<UpdateUserResponseDTO> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {

        UpdateUserResponseDTO error = new UpdateUserResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()

        );

        return ResponseEntity.badRequest().body(error);
    }



    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<UpdateUserResponseDTO> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        UpdateUserResponseDTO body = new UpdateUserResponseDTO(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage()
        );
        return  ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<UpdateUserResponseDTO> handleRuntime(RuntimeException ex, HttpServletRequest request) {

        UpdateUserResponseDTO error = new UpdateUserResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
