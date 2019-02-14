package de.hhu.propra.sharingplatform.security;

import de.hhu.propra.sharingplatform.model.User;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PersonService implements UserDetailsService {

    @Autowired
    private PersonProvider users;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = users.findByEmail(email);
        if (user.isPresent()) {
            User u = user.get();

            UserDetails userdetails =  org.springframework.security.core.userdetails.User.builder()
                .username(u.getEmail())
                .password("$2a$08$MbCSKfkg1C9A6mx82wwVneBpUkyW1ZwhsEjorhqkMYrhRxLJDZ9yO")
                .authorities("Admin")
                .build();
            return userdetails;
        }
        throw new UsernameNotFoundException("Invalid Username");

    }
}
