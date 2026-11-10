package com.stockflow.controller;

import com.stockflow.security.CustomUserDetails;
import com.stockflow.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboard;

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String show(@AuthenticationPrincipal CustomUserDetails u, Model m) {
        m.addAttribute("d", dashboard.build(u.getOrganizationId()));
        return "dashboard";
    }
}