package com.RentalApplication.rent.service.Exceptions;

public class AccessDeniedException extends RuntimeException{

    public AccessDeniedException(String message) {
        super(message);
    }

}
