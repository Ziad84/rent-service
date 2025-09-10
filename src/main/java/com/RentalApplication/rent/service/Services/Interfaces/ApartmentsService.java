package com.RentalApplication.rent.service.Services.Interfaces;

import com.RentalApplication.rent.service.DTO.ApartmentsDTO;

import java.util.List;

public interface ApartmentsService {


   List<ApartmentsDTO> getAllApartments();
    ApartmentsDTO getApartmentById(Integer id);

    ApartmentsDTO createApartment(ApartmentsDTO dto);

    ApartmentsDTO updateApartment(Integer id, ApartmentsDTO dto);

    void deleteApartment(Integer id);


    List<ApartmentsDTO> viewAvailableApartments();

 ApartmentsDTO rentApartment(Integer apartmentId);




}
