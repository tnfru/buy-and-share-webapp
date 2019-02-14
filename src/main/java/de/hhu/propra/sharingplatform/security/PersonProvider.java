package de.hhu.propra.sharingplatform.security;

import de.hhu.propra.sharingplatform.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PersonProvider extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
}
