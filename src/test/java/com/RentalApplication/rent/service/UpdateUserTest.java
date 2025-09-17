package com.RentalApplication.rent.service;

import com.RentalApplication.rent.service.DTO.UserDTO;
import com.RentalApplication.rent.service.Entity.Roles;
import com.RentalApplication.rent.service.Entity.User;
import com.RentalApplication.rent.service.Exceptions.AccessDeniedException;
import com.RentalApplication.rent.service.Repository.UserRepository;
import com.RentalApplication.rent.service.Services.Implementation.UserServiceImpl;
import com.RentalApplication.rent.service.Utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    @InjectMocks
    private UserServiceImpl usersService;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(100); // current logged-in user's id
        currentUser.setName("Old Name");
        currentUser.setEmail("old@mail.com");
        currentUser.setPassword("$2a$10$oldhash");
        currentUser.setPhoneNumber("0500000000");
        currentUser.setIsDeleted(false);
        currentUser.setCreatedAt(LocalDateTime.now().minusDays(1));
        currentUser.setUpdatedAt(LocalDateTime.now().minusDays(1));

        Roles role = new Roles();
        role.setId(3);
        role.setName("Client");
        currentUser.setRole(role);
    }

    @Test
    void updateUser_success() {

        UserDTO dto = UserDTO.builder()
                .name("New Name")
                .email("new@mail.com")
                .password("newStrongPass")
                .phoneNumber("0555555555")
                .build();


        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository))
                    .thenReturn(currentUser);

            when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);
            when(userRepository.existsByPhoneNumber("0555555555")).thenReturn(false);
            when(passwordEncoder.encode("newStrongPass")).thenReturn("ENCODED");
             when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));


            UserDTO result = usersService.updateUser(currentUser.getId(), dto);


            verify(userRepository).existsByEmail("new@mail.com");
            verify(userRepository).existsByPhoneNumber("0555555555");
            verify(passwordEncoder).encode("newStrongPass");
            verify(userRepository).save(any(User.class));

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            User saved = captor.getValue();

            assertThat(saved.getName()).isEqualTo("New Name");
            assertThat(saved.getEmail()).isEqualTo("new@mail.com");
            assertThat(saved.getPhoneNumber()).isEqualTo("0555555555");
            assertThat(saved.getPassword()).isEqualTo("ENCODED");
            assertThat(saved.getUpdatedAt()).isNotNull();

            assertThat(result.getId()).isEqualTo(currentUser.getId());
            assertThat(result.getName()).isEqualTo("New Name");
            assertThat(result.getEmail()).isEqualTo("new@mail.com");
            assertThat(result.getPhoneNumber()).isEqualTo("0555555555");
        }
    }

    @Test
    void updateUser_throwsAccessDenied() {
        UserDTO dto = UserDTO.builder()
                .name("X")
                .email("x@mail.com")
                .password("12345678")
                .phoneNumber("0500000001")
                .build();

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository))
                    .thenReturn(currentUser);

            assertThatThrownBy(() -> usersService.updateUser(999, dto))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("You can't update another user account");

            verifyNoInteractions(passwordEncoder);
            verify(userRepository, never()).save(any());
        }
    }

    @Test
    void updateUser_throws_EmailAlreadyUsed() {
        UserDTO dto = UserDTO.builder()
                .name("New Name")
                .email("dup@mail.com")
                .password("12345678")
                .phoneNumber("0555555555")
                .build();

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository))
                    .thenReturn(currentUser);

            when(userRepository.existsByEmail("dup@mail.com")).thenReturn(true);

            assertThatThrownBy(() -> usersService.updateUser(currentUser.getId(), dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email is already used by another user");

            verify(userRepository, never()).existsByPhoneNumber(anyString());
            verifyNoInteractions(passwordEncoder);
            verify(userRepository, never()).save(any());
        }
    }

    @Test
    void updateUser_throws_PhoneAlreadyUsed() {
        UserDTO dto = UserDTO.builder()
                .name("New Name")
                .email("new@mail.com")
                .password("12345678")
                .phoneNumber("0500000000")
                .build();

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getCurrentUser(userRepository))
                    .thenReturn(currentUser);

            when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);
            when(userRepository.existsByPhoneNumber("0500000000")).thenReturn(true);

            assertThatThrownBy(() -> usersService.updateUser(currentUser.getId(), dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Phone number is already used by another user");

            verifyNoInteractions(passwordEncoder);
            verify(userRepository, never()).save(any());
        }
    }
}
