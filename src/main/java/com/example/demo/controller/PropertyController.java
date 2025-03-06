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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
public class PropertyController {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserService userService;

    // Define upload directory relative to project root
    private static final String UPLOAD_DIR = "uploads"; // Will resolve to <project_root>/uploads/

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
    public String addProperty(@ModelAttribute Property property,
                              @RequestParam("coverImage") MultipartFile coverImage,
                              Authentication authentication,
                              Model model) {
        try {
            // Set the host
            String username = authentication.getName();
            User host = userService.findByUsername(username);
            if (host == null) {
                throw new RuntimeException("Host not found for username: " + username);
            }
            property.setHost(host);

            // Handle file upload
            if (!coverImage.isEmpty()) {
                String contentType = coverImage.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    model.addAttribute("error", "Please upload an image file.");
                    return "add-property";
                }

                // Use absolute path based on project root
                String projectRoot = System.getProperty("user.dir"); // Gets project root
                Path uploadPath = Paths.get(projectRoot, UPLOAD_DIR); // <project_root>/uploads/
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Save the file
                String fileName = UUID.randomUUID() + "_" + coverImage.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                coverImage.transferTo(filePath.toFile());
                property.setCoverImagePath("/uploads/" + fileName); // Relative URL for web access
            }

            // Save the property
            propertyRepository.save(property);
            return "redirect:/";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload image: " + e.getMessage());
            return "add-property";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add property: " + e.getMessage());
            return "add-property";
        }
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