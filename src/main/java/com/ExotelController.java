package com.restaurant.caller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.caller.service.ExotelService;

@RestController
@RequestMapping("/exotel")
public class ExotelController {

    private final ExotelService exotelService;

    public ExotelController(ExotelService exotelService) {
        this.exotelService = exotelService;
    }

    @GetMapping("/call")
    public String call(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String exoPhone) throws Exception {

        return exotelService.makeCall(
                from,
                to,
                exoPhone
        );
    }
}