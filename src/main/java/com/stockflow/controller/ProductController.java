package com.stockflow.controller;

import com.stockflow.dto.ProductForm;
import com.stockflow.entity.Product;
import com.stockflow.security.CustomUserDetails;
import com.stockflow.service.ProductService;
import com.stockflow.service.SettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;
    private final SettingsService settings;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @AuthenticationPrincipal CustomUserDetails u, Model m) {
        m.addAttribute("products", service.list(u.getOrganizationId(), q));
        m.addAttribute("q", q);
        m.addAttribute("defaultThreshold", settings.getThreshold(u.getOrganizationId()));
        return "products/list";
    }

    @GetMapping("/new")
    public String newForm(Model m) {
        m.addAttribute("form", new ProductForm());
        m.addAttribute("action", "/products");
        return "products/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") ProductForm form, BindingResult br,
                         @AuthenticationPrincipal CustomUserDetails u, Model m) {
        m.addAttribute("action", "/products");
        if (br.hasErrors()) {
            return "products/form";
        }
        try {
            service.create(u.getOrganizationId(), u.getUsername(), form);
        } catch (IllegalArgumentException e) {
            br.rejectValue("sku", "duplicate", e.getMessage());
            return "products/form";
        }
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal CustomUserDetails u, Model m) {
        Product p = service.get(u.getOrganizationId(), id);
        ProductForm f = new ProductForm();
        f.setName(p.getName());
        f.setSku(p.getSku());
        f.setDescription(p.getDescription());
        f.setQuantityOnHand(p.getQuantityOnHand());
        f.setCostPrice(p.getCostPrice());
        f.setSellingPrice(p.getSellingPrice());
        f.setLowStockThreshold(p.getLowStockThreshold());
        m.addAttribute("form", f);
        m.addAttribute("action", "/products/" + id);
        return "products/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") ProductForm form, BindingResult br,
                         @AuthenticationPrincipal CustomUserDetails u, Model m) {
        m.addAttribute("action", "/products/" + id);
        if (br.hasErrors()) {
            return "products/form";
        }
        try {
            service.update(u.getOrganizationId(), id, u.getUsername(), form);
        } catch (IllegalArgumentException e) {
            br.rejectValue("sku", "duplicate", e.getMessage());
            return "products/form";
        }
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails u) {
        service.delete(u.getOrganizationId(), id);
        return "redirect:/products";
    }

    @PostMapping("/{id}/adjust")
    public String adjust(@PathVariable Long id, @RequestParam int delta,
                         @AuthenticationPrincipal CustomUserDetails u) {
        try {
            service.adjust(u.getOrganizationId(), id, delta, u.getUsername());
        } catch (IllegalArgumentException ignored) {
            // stock would go below zero: leave quantity unchanged
        }
        return "redirect:/products";
    }
}