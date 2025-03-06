package com.example.demo.controller;



import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userService.registerUser(user);
        return "redirect:/login"; // Redirect to login after registration
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // API endpoint (optional, for testing)
    @PostMapping("/api/users/register")
    @ResponseBody
    public User registerApi(@RequestBody User user) {
        return userService.registerUser(user);
    }
}