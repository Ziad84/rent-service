package com.RentalApplication.rent.service.Services.Interfaces;

import com.RentalApplication.rent.service.DTO.AppartmentsDTO;

import java.util.List;

public interface AppartmentsService {


    List<AppartmentsDTO> getAllApartments(String currentUserRole, Integer currentUserId);
    AppartmentsDTO getApartmentById(Integer id, String currentUserRole, Integer currentUserId);

    AppartmentsDTO createApartment(AppartmentsDTO dto, String currentUserRole, Integer currentUserId);
    AppartmentsDTO updateApartment(Integer id, AppartmentsDTO dto, String currentUserRole, Integer currentUserId);
    void deleteApartment(Integer id, String currentUserRole, Integer currentUserId);

    List<AppartmentsDTO> viewAvailableApartments();
    AppartmentsDTO rentApartment(Integer apartmentId, Integer clientId);
}
