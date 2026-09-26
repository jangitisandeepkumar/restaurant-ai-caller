package com.restaurant.caller.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurant-call")
public class RestaurantCallController {

    private final ExotelService exotelService;

    public RestaurantCallController(
            ExotelService exotelService) {

        this.exotelService = exotelService;
    }

    @GetMapping("/call")
    public String callRestaurant(
            @RequestParam String phoneNumber)
            throws Exception {

        String fromNumber = "09502272219";

        String exoPhone = "04049170144";

        return exotelService.makeCall(
                fromNumber,
                phoneNumber,
                exoPhone
        );
    }
}