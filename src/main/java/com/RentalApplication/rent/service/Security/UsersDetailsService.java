package com.RentalApplication.rent.service.Security;

import com.RentalApplication.rent.service.Entity.Users;
import com.RentalApplication.rent.service.Repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@Configuration
@RequiredArgsConstructor
public class UsersDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

   /* @Bean
    public UserDetailsService userDetailsService(){
        return username -> (UserDetails) usersRepository.findByEmail(username)
                .orElseThrow(()->new UsernameNotFoundException("User not found"));

        }
*/

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



