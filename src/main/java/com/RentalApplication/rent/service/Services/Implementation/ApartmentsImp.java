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
                .rentedAt(apartment.getRentedAt());

        // Show owner info only for ADMIN and OWNER roles
        if (!Client.equals(currentUserRole)) {
            builder.ownerId(apartment.getOwner().getId());
        }



        // Show client info only for ADMIN and involved parties
        if (apartment.getClient() != null) {
            builder.clientId(apartment.getClient().getId());
        }

        // Show deletion status only to ADMIN
        if (Admin.equals(currentUserRole)) {
            builder.isDeleted(apartment.getIsDeleted());
        }

        return builder.build();
    }




    @Override
    public List<ApartmentsDTO> getAllApartments() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetails ud)) {
            throw new AccessDeniedException("Unauthenticated");
        }

        // from principal
        String email = ((UserDetails) auth.getPrincipal()).getUsername();

        // derive role name from authorities (ROLE_Owner -> Owner)
        String currentUserRole = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)           // e.g. ROLE_Owner
                .findFirst()                                   // assuming single role
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .orElseThrow(() -> new RuntimeException("No role assigned"));

        // need the user id -> load once by email
        User current = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Integer currentUserId = current.getId();

        List<Apartments> apartments = apartmentsRepository.findAll();

        return apartments.stream()
                .filter(apartment -> {
                    if (Admin.equals(currentUserRole)) {
                        return true; // Admin sees all (including deleted)
                    }
                    if (Owner.equals(currentUserRole)) {
                        // Owner sees all of THEIR apartments (rented or not), but not deleted
                        return !apartment.getIsDeleted();

                    }
                    if (Client.equals(currentUserRole)) {
                        // Client sees ALL non-deleted apartments (even if rented by someone else)
                        return !apartment.getIsDeleted();
                    }
                    return false;
                })
                .map(apartment -> mapApartmentToDTO(apartment, currentUserRole))
                .collect(java.util.stream.Collectors.toList());
    }



    @Override
    public List<ApartmentsDTO> viewAvailableApartments() {
        // figure out the caller's role label: "Admin" / "Owner" / "Client"
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Unauthenticated");
        }
        String currentUserRole = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)   // e.g. ROLE_Owner
                .findFirst()
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)                    // → Owner/Admin/Client
                .orElse("Client"); // safe default

        return apartmentsRepository.findByIsDeleted(false).stream()
                .filter(a -> a.getClient() == null)
                .filter(a -> a.getMonthlyRent() != null && a.getMonthlyRent() > 0)
                .filter(a -> a.getRoomsNumber()  != null && a.getRoomsNumber()  > 0)
                .map(a -> {
                    ApartmentsDTO dto = mapApartmentToDTO(a, currentUserRole);
                    // ensure clients see the owner on "available" list
                    dto.setOwnerId(a.getOwner().getId());
                    return dto;
                })
                .toList();

    }

    @Override
    public ApartmentsDTO getApartmentById(Integer id) {
        // If role/id not provided, resolve from the current principal once here
        if (id == null) {
            throw new IllegalArgumentException("Apartment ID cannot be null");
        }

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof UserDetails ud)) {
            throw new  AccessDeniedException("Unauthenticated");
        }

        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        User current = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role  = current.getRole().getName();  // "Admin" / "Owner" / "Client"
        Integer userId = current.getId();

        Apartments apartment = apartmentsRepository.findById(id)
                .orElseThrow(() -> new  ApartmentNotFoundException(
                        "Apartment not found with id: " + id));

        // If apartment is deleted, only Admin can see it
        if (Boolean.TRUE.equals(apartment.getIsDeleted()) && !Admin.equals(role)) {
            throw new  ApartmentNotFoundException(
                    "Apartment not found with id: " + id);
        }


        // Client: any non-deleted apartment is fine (handled by the deleted check above)

        return mapApartmentToDTO(apartment, role);
    }



    @Override
    public ApartmentsDTO createApartment(ApartmentsDTO dto) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetails ud)) {
            throw new AccessDeniedException("Unauthenticated");
        }
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        User current = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String roleName = current.getRole().getName(); // "Owner"/"Admin"/"Client"
        if (!Owner.equals(roleName)) {
            throw new AccessDeniedException(
                    "Only Owner can create apartments");
        }

        // Prevent creating on behalf of someone else
        if (dto.getOwnerId() != null && !dto.getOwnerId().equals(current.getId())) {
            throw new AccessDeniedException(
                    "You can only create apartments for your own account");
        }

        // Validate fields (you already have this helper)
        validateApartmentData(dto);

        Apartments apartment = Apartments.builder()
                .title(dto.getTitle())
                .monthlyRent(dto.getMonthlyRent())
                .roomsNumber(dto.getRoomsNumber())
                .owner(current)               // force the creator as owner
                .isDeleted(false)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        Apartments saved = apartmentsRepository.save(apartment);
        return mapApartmentToDTO(saved, Owner);

    }


    @Override
    public ApartmentsDTO updateApartment(Integer id, ApartmentsDTO dto) {
        if (id == null) throw new IllegalArgumentException("Apartment ID cannot be null");
        validateApartmentData(dto); // your existing field validator

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetails ud)) {
            throw new com.RentalApplication.rent.service.Exceptions.AccessDeniedException("Unauthenticated");
        }

        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        User current = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Belt-and-suspenders: only Owner can update (even if another controller calls this)
        if (!Owner.equals(current.getRole().getName())) {
            throw new com.RentalApplication.rent.service.Exceptions.AccessDeniedException("Only Owner can update apartments");
        }

        Apartments apartment = apartmentsRepository.findById(id)
                .orElseThrow(() -> new com.RentalApplication.rent.service.Exceptions.ApartmentNotFoundException("Apartment not found"));

        // Must be the owner of this apartment
        if (!apartment.getOwner().getId().equals(current.getId())) {
            throw new com.RentalApplication.rent.service.Exceptions.AccessDeniedException("You can only update your own apartments");
        }

        // No updates on deleted or rented apartments
        if (Boolean.TRUE.equals(apartment.getIsDeleted())) {
            throw new com.RentalApplication.rent.service.Exceptions.ApartmentNotFoundException("Apartment has been deleted");
        }
        if (apartment.getClient() != null) {
            throw new IllegalStateException("Cannot modify a rented apartment");
        }

        // Apply ONLY allowed fields; ignore any ownerId/clientId/isDeleted in DTO
        apartment.setTitle(dto.getTitle());
        apartment.setMonthlyRent(dto.getMonthlyRent());
        apartment.setRoomsNumber(dto.getRoomsNumber());
        apartment.setUpdatedAt(java.time.LocalDateTime.now());

        Apartments saved = apartmentsRepository.save(apartment);
        return mapApartmentToDTO(saved, Owner); // role label for mapper
    }



    @Override
   public void deleteApartment(Integer id) {
     if (id == null) {
        throw new IllegalArgumentException("Apartment ID cannot be null");
    }

    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetails ud)) {
        throw new com.RentalApplication.rent.service.Exceptions.AccessDeniedException("Unauthenticated");
    }

    String email = ((UserDetails) auth.getPrincipal()).getUsername();
    User current = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    String roleName = current.getRole().getName(); // "Admin" / "Owner"

    Apartments apartment = apartmentsRepository.findById(id)
            .orElseThrow(() -> new com.RentalApplication.rent.service.Exceptions.ApartmentNotFoundException(
                    "Apartment not found with id: " + id));

    // already deleted?
    if (Boolean.TRUE.equals(apartment.getIsDeleted())) {
        throw new IllegalStateException("Apartment is already deleted");
    }

    // permissions: Owner can only delete their own; Admin can delete any
    if (Owner.equals(roleName) && !apartment.getOwner().getId().equals(current.getId())) {
        throw new com.RentalApplication.rent.service.Exceptions.AccessDeniedException(
                "You can only delete your own apartments");
    }
    if (!Owner.equals(roleName) && !Admin.equals(roleName)) {
        throw new com.RentalApplication.rent.service.Exceptions.AccessDeniedException(
                "Only Admin and Owner can delete apartments");
    }

    // if rented → free the client before soft delete (client_id -> NULL)
    if (apartment.getClient() != null) {
        apartment.setClient(null);
        // optional: also clear rentedAt if you don’t want history on deleted units
        // apartment.setRentedAt(null);
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

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetails)) {
            throw new AccessDeniedException("Unauthenticated");
        }

        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Must be a Client (defense-in-depth in case someone calls the service directly)
        if (!Client.equals(client.getRole().getName())) {
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



        apartment.setClient(client);
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