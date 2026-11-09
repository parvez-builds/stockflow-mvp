package com.stockflow.service;

import com.stockflow.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettingsService {
    private final OrganizationRepository orgs;

    @Transactional(readOnly = true)
    public int getThreshold(Long orgId) {
        return orgs.findById(orgId).orElseThrow().getDefaultLowStockThreshold();
    }

    @Transactional
    public void setThreshold(Long orgId, int value) {
        orgs.findById(orgId).orElseThrow().setDefaultLowStockThreshold(value);
    }
}