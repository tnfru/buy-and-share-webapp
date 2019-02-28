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

    private final UserRepo users;

    @Autowired
    public SecurityService(UserRepo users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String accountName) throws UsernameNotFoundException {
        Optional<User> userOptional = users.findByAccountName(accountName);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            return org.springframework.security.core.userdetails.User.builder()
                .username(user.getAccountName())
                .password(user.getPasswordHash())
                .roles(user.getRole())
                .build();
        }
        throw new UsernameNotFoundException("Invalid Username");

    }
}
