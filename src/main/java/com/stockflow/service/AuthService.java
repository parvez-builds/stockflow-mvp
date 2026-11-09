package com.stockflow.service;

import com.stockflow.dto.SignupForm;
import com.stockflow.entity.AppUser;
import com.stockflow.entity.Organization;
import com.stockflow.repository.AppUserRepository;
import com.stockflow.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final OrganizationRepository orgs;
    private final AppUserRepository users;
    private final PasswordEncoder encoder;

    @Transactional
    public void signup(SignupForm f) {
        String email = f.getEmail().trim().toLowerCase();

        if (!f.getPassword().equals(f.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (users.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        Organization org = new Organization();
        org.setName(f.getOrganizationName().trim());
        orgs.save(org);

        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(f.getPassword()));
        user.setOrganization(org);
        users.save(user);
    }
}