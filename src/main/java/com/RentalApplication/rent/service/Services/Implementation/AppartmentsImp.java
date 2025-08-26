package com.RentalApplication.rent.service.Services.Implementation;

import com.RentalApplication.rent.service.Entity.Appartments;
import com.RentalApplication.rent.service.Entity.Users;
import com.RentalApplication.rent.service.Repository.AppartmentsRepository;
import com.RentalApplication.rent.service.Repository.UsersRepository;
import com.RentalApplication.rent.service.Services.Interfaces.AppartmentsService;
import com.RentalApplication.rent.service.DTO.AppartmentsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppartmentsImp implements AppartmentsService {

    private final AppartmentsRepository apartmentsRepository;
    private final UsersRepository userRepository;

    private AppartmentsDTO mapApartmentToDTO(Appartments apartment, String currentUserRole) {
        AppartmentsDTO dto = AppartmentsDTO.builder()
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
    public List<AppartmentsDTO> getAllApartments(String currentUserRole, Integer currentUserId) {
        return apartmentsRepository.findAll()
                .stream()
                .filter(a -> !a.getIsDeleted())
                .map(a -> mapApartmentToDTO(a, currentUserRole))
                .collect(Collectors.toList());
    }

    @Override
    public AppartmentsDTO getApartmentById(Integer id, String currentUserRole, Integer currentUserId) {
        Appartments apartment = apartmentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apartment not found"));
        return mapApartmentToDTO(apartment, currentUserRole);
    }

    @Override
    public AppartmentsDTO createApartment(AppartmentsDTO dto, String currentUserRole, Integer currentUserId) {
        if (!"OWNER".equals(currentUserRole) && !"ADMIN".equals(currentUserRole)) {
            throw new RuntimeException("Permission denied");
        }

        Users owner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Appartments apartment = Appartments.builder()
                .title(dto.getTitle())
                .monthlyRent(dto.getMonthlyRent())
                .roomsNumber(dto.getRoomsNumber())
                .owner(owner)
                .isDeleted(false)
                .build();

        return mapApartmentToDTO(apartmentsRepository.save(apartment), currentUserRole);
    }

    @Override
    public AppartmentsDTO updateApartment(Integer id, AppartmentsDTO dto, String currentUserRole, Integer currentUserId) {
        Appartments apartment = apartmentsRepository.findById(id)
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
    public void deleteApartment(Integer id, String currentUserRole, Integer currentUserId) {
        Appartments apartment = apartmentsRepository.findById(id)
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
    public List<AppartmentsDTO> viewAvailableApartments() {
        return apartmentsRepository.findAll()
                .stream()
                .filter(a -> !a.getIsDeleted() && a.getClient() == null)
                .map(a -> mapApartmentToDTO(a, "CLIENT"))
                .collect(Collectors.toList());
    }

    @Override
    public AppartmentsDTO rentApartment(Integer apartmentId, Integer clientId) {
        Appartments apartment = apartmentsRepository.findById(apartmentId)
                .orElseThrow(() -> new RuntimeException("Apartment not found"));

        if (apartment.getClient() != null) {
            throw new RuntimeException("Apartment is already rented");
        }

        Users client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        apartment.setClient(client);
        apartment.setRentedAt(LocalDateTime.now());

        Appartments savedApartment = apartmentsRepository.save(apartment);

        return mapApartmentToDTO(savedApartment, "CLIENT");


    }

    }
