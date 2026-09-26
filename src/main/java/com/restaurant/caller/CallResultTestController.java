package com.restaurant.caller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class CallResultTestController {

    private final OpenAiService openAiService;

    public CallResultTestController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @GetMapping("/extract-result")
    public String extractResult(@RequestParam String conversation) {

        try {

            return openAiService.extractCallResult(conversation);

        } catch (Exception e) {

            e.printStackTrace();

            return "RESULT EXTRACTION ERROR:\n"
                    + e.getClass().getName()
                    + "\n\nMESSAGE:\n"
                    + e.getMessage();
        }
    }
}