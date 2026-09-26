package com.restaurant.caller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    @GetMapping
    public String test() {
        return "Restaurant Controller is working!";
    }

    @PostMapping
    public Restaurant addRestaurant(@RequestBody Restaurant restaurant) {

        System.out.println("Restaurant Name: " + restaurant.getRestaurant());
        System.out.println("Phone Number: " + restaurant.getPhoneNumber());
        System.out.println("Promotion: " + restaurant.getPromotion());
        System.out.println("Payment: " + restaurant.getPayment());
        System.out.println("Day: " + restaurant.getIfYesDay());
        System.out.println("Status: " + restaurant.getStatus());

        return restaurant;
    }
}