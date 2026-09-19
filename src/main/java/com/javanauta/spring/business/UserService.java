package com.javanauta.spring.business;

import com.javanauta.spring.infrastructure.entity.User;
import com.javanauta.spring.infrastructure.exceptions.ConflictException;
import com.javanauta.spring.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        try {
            emailExists(user.getEmail());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } catch (ConflictException e) {
            throw new ConflictException("E-mail already exists.", e.getCause());
        }
        return userRepository.save(user);
    }

    public void emailExists(String email) {
        try {
            boolean exists = verifyEmailExists(email);
            if(exists) throw new ConflictException("E-mail already exists." + email);
        } catch (ConflictException e) {
            throw new ConflictException("E-mail already exists." + e.getCause());
        }
    }

    public boolean verifyEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + email));
    }

    public void deleteUserByEmail(String email) {
        userRepository.deleteByEmail(email);
    }

}
