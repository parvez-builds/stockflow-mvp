package com.stockflow.security;

import com.stockflow.entity.AppUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class CustomUserDetails extends User {
    private final Long organizationId;

    public CustomUserDetails(AppUser u) {
        super(u.getEmail(), u.getPasswordHash(),
              List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.organizationId = u.getOrganization().getId();
    }

    public Long getOrganizationId() {
        return organizationId;
    }
}