package com.example.demo.controller;

import com.example.demo.model.Booking;
import com.example.demo.model.Property;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.PropertyRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/book/{propertyId}")
    public String showBookingForm(@PathVariable Long propertyId, Model model, Authentication authentication) {
        Property property = propertyRepository.findById(propertyId).orElse(null);
        if (property == null) {
            return "redirect:/error";
        }

        model.addAttribute("property", property);
        model.addAttribute("booking", new Booking());

        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("requiresLogin", true);
        }

        return "book-property";
    }

    @PostMapping("/book/{propertyId}")
    public String submitBooking(@PathVariable Long propertyId,
                                @ModelAttribute Booking booking,
                                Authentication authentication,
                                Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login?redirect=/book/" + propertyId;
        }

        Property property = propertyRepository.findById(propertyId).orElse(null);
        if (property == null) {
            return "redirect:/error";
        }

        String username = authentication.getName();
        booking.setGuest(userService.findByUsername(username));
        booking.setProperty(property);

        if (booking.getCheckInDate().isAfter(booking.getCheckOutDate())) {
            model.addAttribute("error", "Check-out date must be after check-in date.");
            model.addAttribute("property", property);
            return "book-property";
        }

        Booking savedBooking = bookingRepository.save(booking);
        return "redirect:/booking-confirmation/" + savedBooking.getId(); // Redirect to confirmation
    }

    @GetMapping("/booking-confirmation/{bookingId}")
    public String showBookingConfirmation(@PathVariable Long bookingId, Model model) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return "redirect:/error";
        }
        model.addAttribute("booking", booking);
        return "booking-confirmation";
    }
}