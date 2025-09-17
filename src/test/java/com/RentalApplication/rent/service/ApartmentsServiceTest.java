package com.RentalApplication.rent.service;

import com.RentalApplication.rent.service.DTO.ApartmentsDTO;
import com.RentalApplication.rent.service.Entity.Apartments;
import com.RentalApplication.rent.service.Entity.Roles;
import com.RentalApplication.rent.service.Entity.User;
import com.RentalApplication.rent.service.Exceptions.ApartmentNotFoundException;
import com.RentalApplication.rent.service.Repository.AppartmentsRepository;
import com.RentalApplication.rent.service.Repository.UserRepository;
import com.RentalApplication.rent.service.Services.Implementation.ApartmentsImp;
import com.RentalApplication.rent.service.Utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApartmentsServiceTest {

    @Mock
    private AppartmentsRepository apartmentsRepository;

    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private ApartmentsImp apartmentsService;

    private User admin, owner1, owner2, client1;
    private Roles ADMIN, OWNER, CLIENT;

    @BeforeEach
    void init() {
        ADMIN = role("Admin");
        OWNER = role("Owner");
        CLIENT = role("Client");

        admin  = user(1, "admin@mail.com", ADMIN);
        owner1 = user(10, "owner1@mail.com", OWNER);
        owner2 = user(11, "owner2@mail.com", OWNER);
        client1= user(100, "client1@mail.com", CLIENT);
    }


    @Test
    void getAllApartments_including_deletedForAdmin() {
        Apartments a1 = apt(1, owner1, null, false);
        Apartments a2 = apt(2, owner2, client1, false);
        Apartments a3 = apt(3, owner1, null, true); // deleted

        when(apartmentsRepository.findAll()).thenReturn(List.of(a1, a2, a3));

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository)).thenReturn(admin);

            List<ApartmentsDTO> out = apartmentsService.getAllApartments();

            assertThat(out).hasSize(3); // Admin sees all
            verify(apartmentsRepository).findAll();
        }
    }

    @Test
    void getAllApartments_withoutdeleted() {
        Apartments a1 = apt(1, owner1, null, false);
        Apartments a2 = apt(2, owner2, client1, false);
        Apartments a3 = apt(3, owner1, null, true);

        when(apartmentsRepository.findAll()).thenReturn(List.of(a1, a2, a3));

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository)).thenReturn(owner1);

            List<ApartmentsDTO> out = apartmentsService.getAllApartments();

            assertThat(out).hasSize(2);
            assertThat(out).extracting(ApartmentsDTO::getId).containsExactlyInAnyOrder(1, 2);
        }
    }




    @Test
    void viewAvailableApartments_allusers() {
        Apartments ok1   = apt(1, owner1, null, false); ok1.setMonthlyRent(1000); ok1.setRoomsNumber(2);
        Apartments rented= apt(2, owner1, client1, false); rented.setMonthlyRent(1200); rented.setRoomsNumber(2);
        Apartments zero$ = apt(3, owner1, null, false); zero$.setMonthlyRent(0); zero$.setRoomsNumber(2);
        Apartments zeroRm= apt(4, owner1, null, false); zeroRm.setMonthlyRent(800); zeroRm.setRoomsNumber(0);
        Apartments del   = apt(5, owner1, null, true); del.setMonthlyRent(700); del.setRoomsNumber(1);

        when(apartmentsRepository.findByIsDeleted(false))
                .thenReturn(List.of(ok1, rented, zero$, zeroRm));

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository)).thenReturn(client1);

            List<ApartmentsDTO> out = apartmentsService.viewAvailableApartments();

            assertThat(out).hasSize(1);
            assertThat(out.get(0).getId()).isEqualTo(1);
            verify(apartmentsRepository).findByIsDeleted(false);
        }
    }


    @Test
    void getApartmentById_adminCanSeeDeleted() {
        Apartments del = apt(99, owner1, null, true);
        when(apartmentsRepository.findById(99)).thenReturn(Optional.of(del));

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository)).thenReturn(admin);

            ApartmentsDTO dto = apartmentsService.getApartmentById(99);
            assertThat(dto.getId()).isEqualTo(99);
            verify(apartmentsRepository).findById(99);
        }
    }

    @Test
    void getApartmentById_IfDeleted_throws_NotFound() {
        Apartments del = apt(50, owner1, null, true);
        when(apartmentsRepository.findById(50)).thenReturn(Optional.of(del));

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository)).thenReturn(client1);

            assertThatThrownBy(() -> apartmentsService.getApartmentById(50))
                    .isInstanceOf(ApartmentNotFoundException.class)
                    .hasMessageContaining("Apartment not found with id: 50");
        }
    }

    @Test
    void getApartmentById_except_null() {
        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository)).thenReturn(admin);
            assertThatThrownBy(() -> apartmentsService.getApartmentById(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void updateApartment_success() {
        ApartmentsDTO dto = ApartmentsDTO.builder()
                .title("New Title").monthlyRent(1500).roomsNumber(3).build();

        Apartments apt = apt(1, owner1, null, false);

        when(apartmentsRepository.findById(1)).thenReturn(Optional.of(apt));
        when(apartmentsRepository.save(any(Apartments.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository)).thenReturn(owner1);

            ApartmentsDTO out = apartmentsService.updateApartment(1, dto);

            assertThat(out.getId()).isEqualTo(1);
            assertThat(apt.getTitle()).isEqualTo("New Title");
            assertThat(apt.getMonthlyRent()).isEqualTo(1500);
            assertThat(apt.getRoomsNumber()).isEqualTo(3);
            verify(apartmentsRepository).save(apt);
        }
    }

    @Test
    void deleteApartment_success() {
        Apartments apt = apt(20, owner1, null, false);
        when(apartmentsRepository.findById(20)).thenReturn(Optional.of(apt));
        when(apartmentsRepository.save(any(Apartments.class))).thenAnswer(inv -> inv.getArgument(0));

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository)).thenReturn(owner1);

            apartmentsService.deleteApartment(20);

            assertThat(apt.getIsDeleted()).isTrue();
            verify(apartmentsRepository).save(apt);
        }
    }

    @Test
    void rentApartment_available() {
        Apartments apt = apt(30, owner1, null, false);
        when(apartmentsRepository.findById(30)).thenReturn(Optional.of(apt));
        when(apartmentsRepository.save(any(Apartments.class))).thenAnswer(inv -> inv.getArgument(0));

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository)).thenReturn(client1);

            ApartmentsDTO dto = apartmentsService.rentApartment(30);

            assertThat(apt.getClient()).isEqualTo(client1);
            assertThat(dto.getId()).isEqualTo(30);
            verify(apartmentsRepository).save(apt);
        }
    }


    private static Roles role(String name) {
        Roles r = new Roles();
        r.setId(switch (name) {
            case "Admin" -> 1;
            case "Owner" -> 2;
            default -> 3;
        });
        r.setName(name);
        return r;
    }

    private static User user(int id, String email, Roles role) {
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setName(email);
        u.setPassword("$2a$hash");
        u.setRole(role);
        u.setIsDeleted(false);
        u.setCreatedAt(LocalDateTime.now().minusDays(1));
        u.setUpdatedAt(LocalDateTime.now().minusHours(1));
        return u;
    }

    private static Apartments apt(int id, User owner, User client, boolean deleted) {
        Apartments a = new Apartments();
        a.setId(id);
        a.setTitle("Apt " + id);
        a.setMonthlyRent(1000);
        a.setRoomsNumber(2);
        a.setOwner(owner);
        a.setClient(client);
        a.setIsDeleted(deleted);
        a.setCreatedAt(LocalDateTime.now().minusDays(3));
        a.setUpdatedAt(LocalDateTime.now().minusDays(2));
        return a;
    }
}
