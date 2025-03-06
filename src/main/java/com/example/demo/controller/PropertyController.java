package com.example.demo.controller;


import com.example.demo.model.Property;
import com.example.demo.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller // Use @Controller instead of @RestController for Thymeleaf
public class PropertyController {

    @Autowired
    private PropertyRepository propertyRepository;

    @GetMapping("/")
    public String home(Model model) {
        return "index"; // Renders src/main/resources/templates/index.html
    }

    @GetMapping("/search")
    public String search(@RequestParam String location, Model model) {
        List<Property> properties = propertyRepository.findByLocationContaining(location);
        model.addAttribute("properties", properties);
        return "index"; // Update index.html to display properties
    }

    @PostMapping("/api/properties")
    @ResponseBody // For REST API response
    public Property addProperty(@RequestBody Property property) {
        return propertyRepository.save(property);
    }

    @GetMapping("/api/properties")
    @ResponseBody // For REST API response
    public List<Property> searchProperties(@RequestParam String location) {
        return propertyRepository.findByLocationContaining(location);
    }
}