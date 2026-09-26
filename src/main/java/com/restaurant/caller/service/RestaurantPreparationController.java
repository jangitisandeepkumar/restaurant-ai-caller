package com.restaurant.caller.service;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.caller.Restaurant;

@RestController

@RequestMapping("/prepare")
public class RestaurantPreparationController {

    private final GoogleSheetsService googleSheetsService;

    public RestaurantPreparationController(GoogleSheetsService googleSheetsService) {
        this.googleSheetsService = googleSheetsService;
    }

    @GetMapping("/next")
    public String prepareNextRestaurant() throws Exception {

        List<Restaurant> restaurants =
                googleSheetsService.getRestaurantObjects();

        if (restaurants.isEmpty()) {
            return "No restaurants found in Google Sheet.";
        }

        Restaurant restaurant = restaurants.get(0);

        return "Restaurant: " + restaurant.getRestaurant() + "\n"
                + "Phone: " + restaurant.getPhoneNumber() + "\n"
                + "Action: READY_TO_CALL";
    }
}