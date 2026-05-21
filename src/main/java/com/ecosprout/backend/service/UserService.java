package com.ecosprout.backend.service;

import com.ecosprout.backend.model.User;
import com.ecosprout.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> getProfileByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User updateUserProfile(String username, Map<String, String> details) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (details.containsKey("firstName")) user.setFirstName(details.get("firstName"));
        if (details.containsKey("lastName")) user.setLastName(details.get("lastName"));
        if (details.containsKey("email")) user.setEmail(details.get("email"));
        if (details.containsKey("addressLine1")) user.setAddressLine1(details.get("addressLine1"));
        if (details.containsKey("addressLine2")) user.setAddressLine2(details.get("addressLine2"));
        if (details.containsKey("city")) user.setCity(details.get("city"));
        if (details.containsKey("postalCode")) user.setPostalCode(details.get("postalCode"));
        if (details.containsKey("country")) user.setCountry(details.get("country"));

        return userRepository.save(user);
    }

    public void updateUserPassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Incorrect old password");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters long");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}