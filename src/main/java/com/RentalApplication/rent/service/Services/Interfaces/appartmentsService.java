package com.RentalApplication.rent.service.Services.Interfaces;

import com.RentalApplication.rent.service.dto.appartmentsDTO;

import java.util.List;
import java.util.UUID;

public interface appartmentsService {


    List<appartmentsDTO> getAllApartments(String currentUserRole, UUID currentUserId);
    appartmentsDTO getApartmentById(UUID id, String currentUserRole, UUID currentUserId);

    appartmentsDTO createApartment(appartmentsDTO dto, String currentUserRole, UUID currentUserId);
    appartmentsDTO updateApartment(UUID id, appartmentsDTO dto, String currentUserRole, UUID currentUserId);
    void deleteApartment(UUID id, String currentUserRole, UUID currentUserId);

    List<appartmentsDTO> viewAvailableApartments();
    appartmentsDTO rentApartment(UUID apartmentId, UUID clientId);
}
