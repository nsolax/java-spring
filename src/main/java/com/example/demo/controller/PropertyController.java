package com.example.demo.controller;

import com.example.demo.model.Property;
import com.example.demo.model.User;
import com.example.demo.repository.PropertyRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PropertyController {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserService userService; // Inject UserService

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam String location, Model model) {
        List<Property> properties = propertyRepository.findByLocationContaining(location);
        model.addAttribute("properties", properties);
        return "index";
    }

    @GetMapping("/add-property")
    public String showAddPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        return "add-property";
    }

    @PostMapping("/add-property")
    public String addProperty(@ModelAttribute Property property, Authentication authentication) {
        String username = authentication.getName(); // Get the logged-in username
        User host = userService.findByUsername(username); // Fetch the User entity
        if (host == null) {
            throw new RuntimeException("Host not found for username: " + username);
        }
        property.setHost(host); // Set the User entity as the host
        propertyRepository.save(property);
        return "redirect:/";
    }

    // Existing API endpoints
    @PostMapping("/api/properties")
    @ResponseBody
    public Property addPropertyApi(@RequestBody Property property) {
        return propertyRepository.save(property);
    }

    @GetMapping("/api/properties")
    @ResponseBody
    public List<Property> searchProperties(@RequestParam String location) {
        return propertyRepository.findByLocationContaining(location);
    }
}