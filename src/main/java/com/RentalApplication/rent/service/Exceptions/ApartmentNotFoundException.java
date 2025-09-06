package com.RentalApplication.rent.service.Exceptions;

public class ApartmentNotFoundException extends RuntimeException{

    public ApartmentNotFoundException(String message) {
        super(message);
    }

}
