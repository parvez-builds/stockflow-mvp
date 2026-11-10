package com.stockflow.controller;

import com.stockflow.security.CustomUserDetails;
import com.stockflow.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {
    private final SettingsService settings;

    @GetMapping
    public String show(@AuthenticationPrincipal CustomUserDetails u, Model m) {
        m.addAttribute("threshold", settings.getThreshold(u.getOrganizationId()));
        return "settings";
    }

    @PostMapping
    public String save(@RequestParam int threshold,
                       @AuthenticationPrincipal CustomUserDetails u) {
        settings.setThreshold(u.getOrganizationId(), Math.max(0, threshold));
        return "redirect:/settings?saved";
    }
}