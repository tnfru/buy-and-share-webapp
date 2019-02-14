package de.hhu.propra.sharingplatform.security;

import de.hhu.propra.sharingplatform.model.User;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PersonService implements UserDetailsService {

    @Autowired
    private PersonProvider users;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = users.findByEmail(email);
        if (user.isPresent()) {
            User user1 = user.get();


            UserDetails userdetails = org.springframework.security.core.userdetails.User.builder()
                .username(user1.getEmail())
                .password(user1.getPasswordHash())
                .authorities("Admin")
                .build();
            return userdetails;
        }
        throw new UsernameNotFoundException("Invalid Username");

    }
}
