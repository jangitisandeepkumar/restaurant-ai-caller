package com.restaurant.caller;

import com.restaurant.caller.service.ExotelVoiceAiService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exotel")
public class ExotelVoiceAiController {

    private final ExotelVoiceAiService exotelVoiceAiService;

    public ExotelVoiceAiController(
            ExotelVoiceAiService exotelVoiceAiService) {

        this.exotelVoiceAiService =
                exotelVoiceAiService;
    }

    @GetMapping("/ai-call")
    public String call(
            @RequestParam String phone,
            @RequestParam String wsUrl) {

        return exotelVoiceAiService.startCall(
                phone,
                wsUrl
        );
    }
}