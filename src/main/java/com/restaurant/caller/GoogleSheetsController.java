package com.restaurant.caller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.caller.service.GoogleSheetsService;

@RestController
@RequestMapping("/sheets")
public class GoogleSheetsController {

    private final GoogleSheetsService googleSheetsService;

    public GoogleSheetsController(
            GoogleSheetsService googleSheetsService) {

        this.googleSheetsService = googleSheetsService;
    }

    // ==========================================
    // Test Google Sheet raw data
    // ==========================================

    @GetMapping("/restaurants")
    public Object getRestaurants() throws Exception {

        return googleSheetsService.getRestaurants();
    }

    // ==========================================
    // Get Restaurant Java objects
    // ==========================================

    @GetMapping("/restaurant-objects")
    public List<Restaurant> getRestaurantObjects()
            throws Exception {

        return googleSheetsService.getRestaurantObjects();
    }

    // ==========================================
    // Test updating Google Sheet
    // ==========================================

    @GetMapping("/test-update")
    public String testUpdate() throws Exception {

        googleSheetsService.updateRestaurantResult(
                2,
                "Yes",
                "500",
                "Monday",
                "Confirmed"
        );

        return "Google Sheet updated successfully!";
    }
}