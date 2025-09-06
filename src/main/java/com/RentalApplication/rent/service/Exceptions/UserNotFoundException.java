package com.RentalApplication.rent.service.Exceptions;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message) {

        super(message);
    }


}
