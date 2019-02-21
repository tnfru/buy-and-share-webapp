package de.hhu.propra.sharingplatform.security;

import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.User;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityService implements UserDetailsService {

    @Autowired
    private UserRepo users;


    @Override
    public UserDetails loadUserByUsername(String accountName) throws UsernameNotFoundException {
        Optional<User> user = users.findByAccountName(accountName);
        if (user.isPresent()) {
            User user1 = user.get();


            UserDetails userdetails = org.springframework.security.core.userdetails.User.builder()
                .username(user1.getAccountName())
                .password(user1.getPasswordHash())
                .authorities("Admin")
                .build();
            return userdetails;
        }
        throw new UsernameNotFoundException("Invalid Username");

    }
}
