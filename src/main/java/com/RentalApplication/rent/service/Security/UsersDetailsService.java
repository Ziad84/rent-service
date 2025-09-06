package com.RentalApplication.rent.service.Security;

import com.RentalApplication.rent.service.Entity.User;
import com.RentalApplication.rent.service.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/*
@Configuration
@RequiredArgsConstructor
public class UsersDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {


        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().getName()) // must match ROLE_ADMIN, ROLE_OWNER, ROLE_CLIENT format if using hasRole()
                .build();
    }
}
*/

@Configuration
@RequiredArgsConstructor
public class UsersDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return buildUserDetails(user);
    }

    public UserDetails loadUserById(Integer id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        return buildUserDetails(user);
    }

    // Helper method to build UserDetails consistently
    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().getName())
                .build();
    }
}











