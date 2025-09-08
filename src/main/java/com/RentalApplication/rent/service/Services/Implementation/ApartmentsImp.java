package com.RentalApplication.rent.service.Services.Implementation;

import com.RentalApplication.rent.service.Entity.Apartments;
import com.RentalApplication.rent.service.Entity.User;
import com.RentalApplication.rent.service.Exceptions.AccessDeniedException;
import com.RentalApplication.rent.service.Exceptions.ApartmentNotFoundException;
import com.RentalApplication.rent.service.Exceptions.UserNotFoundException;
import com.RentalApplication.rent.service.Repository.AppartmentsRepository;
import com.RentalApplication.rent.service.Repository.UserRepository;
import com.RentalApplication.rent.service.Services.Interfaces.ApartmentsService;
import com.RentalApplication.rent.service.DTO.ApartmentsDTO;
import com.RentalApplication.rent.service.Utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.RentalApplication.rent.service.Roles.Role.*;

@Service
@RequiredArgsConstructor
public class ApartmentsImp implements ApartmentsService {

    private final AppartmentsRepository apartmentsRepository;
    private final UserRepository userRepository;



    private ApartmentsDTO mapApartmentToDTO(Apartments apartment, String currentUserRole) {
        if (apartment == null) {
            throw new IllegalArgumentException("Apartment cannot be null");
        }

        ApartmentsDTO.ApartmentsDTOBuilder builder = ApartmentsDTO.builder()
                .id(apartment.getId())
                .title(apartment.getTitle())
                .monthlyRent(apartment.getMonthlyRent())
                .roomsNumber(apartment.getRoomsNumber())
                .ownerId(apartment.getOwner().getId())
                .rentedAt(apartment.getRentedAt());





      if (apartment.getClient() != null) {
            builder.clientId(apartment.getClient().getId());
        }

        if (Admin.equals(currentUserRole)) {
            builder.isDeleted(apartment.getIsDeleted());
        }

        return builder.build();
    }




    @Override
    public List<ApartmentsDTO> getAllApartments() {
        User current = SecurityUtils.getCurrentUser(userRepository);
        String roleName = current.getRole().getName();


        List<Apartments> apartments = apartmentsRepository.findAll();

        return apartments.stream()
                .filter(apartment -> {
                    if (Admin.equals(roleName)) {
                        return true;
                    }
                    if (Owner.equals(roleName)) {
                        return !apartment.getIsDeleted();
                    }
                    if (Client.equals(roleName)) {
                        return !apartment.getIsDeleted();
                    }
                    return false;
                })
                .map(apartment -> mapApartmentToDTO(apartment, roleName))
                .collect(Collectors.toList());
    }



    @Override
    public List<ApartmentsDTO> viewAvailableApartments() {

        User current = SecurityUtils.getCurrentUser(userRepository);
        String roleName = current.getRole().getName();

        return apartmentsRepository.findByIsDeleted(false).stream()
                .filter(a -> a.getClient() == null)
                .filter(a -> a.getMonthlyRent() != null && a.getMonthlyRent() > 0)
                .filter(a -> a.getRoomsNumber()  != null && a.getRoomsNumber() > 0)
                .map(a -> {
                    ApartmentsDTO dto = mapApartmentToDTO(a, roleName);
                    return dto;
                })
                .toList();

    }

    @Override
    public ApartmentsDTO getApartmentById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Apartment ID cannot be null");
        }

        User current = SecurityUtils.getCurrentUser(userRepository);
        String roleName = current.getRole().getName();

        Apartments apartment = apartmentsRepository.findById(id)
                .orElseThrow(() -> new  ApartmentNotFoundException("Apartment not found with id: " + id));


        if (Boolean.TRUE.equals(apartment.getIsDeleted()) && !Admin.equals(roleName)) {
            throw new  ApartmentNotFoundException("Apartment not found with id: " + id);

        }



        return mapApartmentToDTO(apartment, roleName);
    }



    @Override
    public ApartmentsDTO createApartment(ApartmentsDTO dto) {
        User current = SecurityUtils.getCurrentUser(userRepository);
        String roleName = current.getRole().getName();

        if (!Owner.equals(roleName)) {
            throw new AccessDeniedException("Only Owner can create apartments");
        }


        if (dto.getOwnerId() != null && !dto.getOwnerId().equals(current.getId())) {
            throw new AccessDeniedException("You can only create apartments for your own account");
        }

        validateApartmentData(dto);

        Apartments apartment = Apartments.builder()
                .title(dto.getTitle())
                .monthlyRent(dto.getMonthlyRent())
                .roomsNumber(dto.getRoomsNumber())
                .owner(current)
                .isDeleted(false)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        Apartments saved = apartmentsRepository.save(apartment);
        return mapApartmentToDTO(saved, Owner);

    }


    @Override
    public ApartmentsDTO updateApartment(Integer id, ApartmentsDTO dto) {
        if (id == null)
            throw new IllegalArgumentException("Apartment ID cannot be null");

        validateApartmentData(dto);
        User current = SecurityUtils.getCurrentUser(userRepository);

        if (!Owner.equals(current.getRole().getName())) {
            throw new AccessDeniedException("Only Owner can update apartments");
        }

        Apartments apartment = apartmentsRepository.findById(id)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment not found"));

        if (!apartment.getOwner().getId().equals(current.getId())) {
            throw new AccessDeniedException("You can only update your own apartments");
        }

        if (Boolean.TRUE.equals(apartment.getIsDeleted())) {
            throw new ApartmentNotFoundException("Apartment has been deleted");
        }

        if (apartment.getClient() != null) {
            throw new IllegalStateException("You can't modify a rented apartment");
        }

        apartment.setTitle(dto.getTitle());
        apartment.setMonthlyRent(dto.getMonthlyRent());
        apartment.setRoomsNumber(dto.getRoomsNumber());
        apartment.setUpdatedAt(java.time.LocalDateTime.now());

        Apartments saved = apartmentsRepository.save(apartment);
        return mapApartmentToDTO(saved, Owner);
    }



    @Override
   public void deleteApartment(Integer id) {
     if (id == null) {
        throw new IllegalArgumentException("Apartment ID cannot be null");
    }

        User current = SecurityUtils.getCurrentUser(userRepository);
        String roleName = current.getRole().getName();

        Apartments apartment = apartmentsRepository.findById(id)
            .orElseThrow(() -> new ApartmentNotFoundException("Apartment not found with id: " + id));


    if (Boolean.TRUE.equals(apartment.getIsDeleted())) {
        throw new IllegalStateException("Apartment is already deleted");
    }


    if (Owner.equals(roleName) && !apartment.getOwner().getId().equals(current.getId())) {
        throw new AccessDeniedException("You can only delete your own apartments");
    }

    if (!Owner.equals(roleName) && !Admin.equals(roleName)) {
        throw new AccessDeniedException("Only Admin and Owner can delete apartments");
    }

    if (apartment.getClient() != null) {
        apartment.setClient(null);

    }

    apartment.setIsDeleted(true);
    apartment.setUpdatedAt(java.time.LocalDateTime.now());
    apartmentsRepository.save(apartment);
}


    @Override
    public ApartmentsDTO rentApartment(Integer apartmentId) {
        if (apartmentId == null) {
            throw new IllegalArgumentException("Apartment ID cannot be null");
        }

        User current = SecurityUtils.getCurrentUser(userRepository);
        String roleName = current.getRole().getName();

        if (!Client.equals(roleName)) {
            throw new AccessDeniedException("Only clients can rent apartments");
        }

        Apartments apartment = apartmentsRepository.findById(apartmentId)
                .orElseThrow(() -> new ApartmentNotFoundException("Apartment not found"));

        if (Boolean.TRUE.equals(apartment.getIsDeleted())) {
            throw new ApartmentNotFoundException("Apartment has been deleted");
        }

        if (apartment.getClient() != null) {
            throw new IllegalStateException("Apartment is already rented");
        }



        apartment.setClient(current);
        apartment.setRentedAt(java.time.LocalDateTime.now());
        apartment.setUpdatedAt(java.time.LocalDateTime.now());

        Apartments saved = apartmentsRepository.save(apartment);
        return mapApartmentToDTO(saved, Client);
    }




    private void validateApartmentData(ApartmentsDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (dto.getMonthlyRent() == null || dto.getMonthlyRent() <= 0) {
            throw new IllegalArgumentException("Monthly rent must be positive");
        }
        if (dto.getRoomsNumber() == null || dto.getRoomsNumber() <= 0) {
            throw new IllegalArgumentException("Number of rooms must be positive");
        }


    }



}