package com.RentalApplication.rent.service;

import com.RentalApplication.rent.service.Entity.Apartments;
import com.RentalApplication.rent.service.Entity.Roles;
import com.RentalApplication.rent.service.Entity.User;
import com.RentalApplication.rent.service.Exceptions.AccessDeniedException;
import com.RentalApplication.rent.service.Repository.AppartmentsRepository;
import com.RentalApplication.rent.service.Repository.UserRepository;
import com.RentalApplication.rent.service.Services.Implementation.UserServiceImpl;
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
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteUserTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private AppartmentsRepository apartmentsRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User current;
    private Roles ownerRole;
    private Roles clientRole;

    @BeforeEach
    void setup() {
        ownerRole = new Roles();
        ownerRole.setId(2);
        ownerRole.setName("Owner");

        clientRole = new Roles();
        clientRole.setId(3);
        clientRole.setName("Client");

        current = new User();
        current.setId(100);
        current.setEmail("me@example.com");
        current.setName("Me");
        current.setPassword("$2a$hash");
        current.setRole(ownerRole);
        current.setIsDeleted(false);
        current.setCreatedAt(LocalDateTime.now().minusDays(1));
        current.setUpdatedAt(LocalDateTime.now().minusDays(1));
    }

    @Test
    void deleteUser_owner_success() {
        current.setRole(ownerRole);

        User dbUser = user(current);

        Apartments a1 = apt(1, dbUser, new User(), false);
        Apartments a2 = apt(2, dbUser, null, false);

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository))
                    .thenReturn(current);

            when(userRepository.findById(current.getId())).thenReturn(Optional.of(dbUser));
            when(apartmentsRepository.findByOwner_Id(dbUser.getId()))
                    .thenReturn(List.of(a1, a2));

            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            userService.deleteUser(current.getId());

            verify(apartmentsRepository).findByOwner_Id(current.getId());
            verify(apartmentsRepository).saveAll(argThat(iterable -> {
                return StreamSupport.stream(iterable.spliterator(), false)
                        .allMatch(a -> Boolean.TRUE.equals(a.getIsDeleted()) && a.getClient() == null);
            }));

            verify(userRepository).save(argThat(u -> Boolean.TRUE.equals(u.getIsDeleted())));
        }
    }

    @Test
    void deleteUser_client_success() {
        current.setRole(clientRole);

        User dbUser = user(current);


        Apartments r1 = apt(10,  new User(), dbUser, false);
        Apartments r2 = apt(11,  new User(), dbUser, false);

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository))
                    .thenReturn(current);

            when(userRepository.findById(current.getId())).thenReturn(Optional.of(dbUser));
            when(apartmentsRepository.findByClient_Id(dbUser.getId()))
                    .thenReturn(List.of(r1, r2));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            userService.deleteUser(current.getId());

            verify(apartmentsRepository).findByClient_Id(current.getId());
            verify(apartmentsRepository).saveAll(argThat(iterable ->
                    StreamSupport.stream(iterable.spliterator(), false)
                            .allMatch(a -> a.getClient() == null)
            ));
            verify(userRepository).save(argThat(u -> Boolean.TRUE.equals(u.getIsDeleted())));
        }
    }

    @Test
    void deleteUser_throws_accessDenied() {
        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository))
                    .thenReturn(current);

            assertThatThrownBy(() -> userService.deleteUser(999))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("You can't delete another user account");

            verifyNoInteractions(apartmentsRepository);
            verify(userRepository, never()).save(any());
        }
    }

    @Test
    void deleteUser_throws_UserNotFound() {
        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository))
                    .thenReturn(current);

            when(userRepository.findById(current.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteUser(current.getId()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("User not found");

            verifyNoInteractions(apartmentsRepository);
            verify(userRepository, never()).save(any());
        }
    }

    @Test
    void deleteUser_throws_AlreadyDeleted() {
        User dbUser = user(current);
        dbUser.setIsDeleted(true);

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository))
                    .thenReturn(current);

            when(userRepository.findById(current.getId())).thenReturn(Optional.of(dbUser));

            assertThatThrownBy(() -> userService.deleteUser(current.getId()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("User is already deleted");

            verifyNoInteractions(apartmentsRepository);
            verify(userRepository, never()).save(any());
        }
    }


    private static User user(User src) {
        User u = new User();
        u.setId(src.getId());
        u.setEmail(src.getEmail());
        u.setName(src.getName());
        u.setPassword(src.getPassword());
        u.setRole(src.getRole());
        u.setIsDeleted(src.getIsDeleted());
        u.setCreatedAt(src.getCreatedAt());
        u.setUpdatedAt(src.getUpdatedAt());
        return u;
    }

    private static Apartments apt(Integer id, User owner, User client, boolean deleted) {
        Apartments a = new Apartments();
        a.setId(id);
        a.setOwner(owner);
        a.setClient(client);
        a.setIsDeleted(deleted);
        a.setTitle("Apt " + id);
        a.setMonthlyRent(1000);
        a.setRoomsNumber(2);
        a.setCreatedAt(LocalDateTime.now().minusDays(2));
        a.setUpdatedAt(LocalDateTime.now().minusDays(1));
        return a;
    }

}
