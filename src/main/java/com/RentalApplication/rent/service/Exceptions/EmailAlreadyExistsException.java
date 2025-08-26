package com.RentalApplication.rent.service.Exceptions;

public class EmailAlreadyExistsException extends  RuntimeException{
    public EmailAlreadyExistsException(String message) {
        super(message);
    }

}
