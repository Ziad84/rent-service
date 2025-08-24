package com.RentalApplication.rent.service.Services.Implementation;

import com.RentalApplication.rent.service.Entity.appartments;
import com.RentalApplication.rent.service.Entity.users;
import com.RentalApplication.rent.service.Repository.AppartmentsRepository;
import com.RentalApplication.rent.service.Repository.usersRepository;
import com.RentalApplication.rent.service.Services.Interfaces.appartmentsService;
import com.RentalApplication.rent.service.dto.appartmentsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class appartmentsImp implements appartmentsService {

    private final AppartmentsRepository apartmentsRepository;
    private final usersRepository userRepository;

    private appartmentsDTO mapApartmentToDTO(appartments apartment, String currentUserRole) {
        appartmentsDTO dto = appartmentsDTO.builder()
                .id(apartment.getId())
                .title(apartment.getTitle())
                .monthlyRent(apartment.getMonthlyRent())
                .roomsNumber(apartment.getRoomsNumber())
                .ownerId(apartment.getOwner().getId())
                .clientId(apartment.getClient() != null ? apartment.getClient().getId() : null)
                .rentedAt(apartment.getRentedAt())
                .build();

        if ("ADMIN".equals(currentUserRole)) {
            dto.setIsDeleted(apartment.getIsDeleted());
        }

        if ("CLIENT".equals(currentUserRole)) {
            dto.setOwnerId(null); // hide owner info from client
        }

        return dto;
    }

    @Override
    public List<appartmentsDTO> getAllApartments(String currentUserRole, UUID currentUserId) {
        return apartmentsRepository.findAll()
                .stream()
                .filter(a -> !a.getIsDeleted())
                .map(a -> mapApartmentToDTO(a, currentUserRole))
                .collect(Collectors.toList());
    }

    @Override
    public appartmentsDTO getApartmentById(UUID id, String currentUserRole, UUID currentUserId) {
        appartments apartment = apartmentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apartment not found"));
        return mapApartmentToDTO(apartment, currentUserRole);
    }

    @Override
    public appartmentsDTO createApartment(appartmentsDTO dto, String currentUserRole, UUID currentUserId) {
        if (!"OWNER".equals(currentUserRole) && !"ADMIN".equals(currentUserRole)) {
            throw new RuntimeException("Permission denied");
        }

        users owner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        appartments apartment = appartments.builder()
                .title(dto.getTitle())
                .monthlyRent(dto.getMonthlyRent())
                .roomsNumber(dto.getRoomsNumber())
                .owner(owner)
                .isDeleted(false)
                .build();

        return mapApartmentToDTO(apartmentsRepository.save(apartment), currentUserRole);
    }

    @Override
    public appartmentsDTO updateApartment(UUID id, appartmentsDTO dto, String currentUserRole, UUID currentUserId) {
        appartments apartment = apartmentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apartment not found"));

        if ("OWNER".equals(currentUserRole) && !apartment.getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException("Permission denied: can only update your own apartments");
        } else if (!"OWNER".equals(currentUserRole) && !"ADMIN".equals(currentUserRole)) {
            throw new RuntimeException("Permission denied");
        }

        apartment.setTitle(dto.getTitle());
        apartment.setMonthlyRent(dto.getMonthlyRent());
        apartment.setRoomsNumber(dto.getRoomsNumber());
        apartment.setRentedAt(dto.getRentedAt());

        return mapApartmentToDTO(apartmentsRepository.save(apartment), currentUserRole);
    }

    @Override
    public void deleteApartment(UUID id, String currentUserRole, UUID currentUserId) {
        appartments apartment = apartmentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apartment not found"));

        if ("OWNER".equals(currentUserRole) && !apartment.getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException("Permission denied: can only delete your own apartments");
        } else if (!"OWNER".equals(currentUserRole) && !"ADMIN".equals(currentUserRole)) {
            throw new RuntimeException("Permission denied");
        }

        apartment.setIsDeleted(true);
        apartmentsRepository.save(apartment);
    }

    @Override
    public List<appartmentsDTO> viewAvailableApartments() {
        return apartmentsRepository.findAll()
                .stream()
                .filter(a -> !a.getIsDeleted() && a.getClient() == null)
                .map(a -> mapApartmentToDTO(a, "CLIENT"))
                .collect(Collectors.toList());
    }

    @Override
    public appartmentsDTO rentApartment(UUID apartmentId, UUID clientId) {
        appartments apartment = apartmentsRepository.findById(apartmentId)
                .orElseThrow(() -> new RuntimeException("Apartment not found"));

        if (apartment.getClient() != null) {
            throw new RuntimeException("Apartment is already rented");
        }

        users client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        apartment.setClient(client);
        apartment.setRentedAt(LocalDateTime.now());

        appartments savedApartment = apartmentsRepository.save(apartment);

        return mapApartmentToDTO(savedApartment, "CLIENT");


    }

    }
