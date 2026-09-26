package com.restaurant.caller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiTestController {

    private final OpenAiService openAiService;

    public AiTestController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @GetMapping("/talk")
    public String talk(@RequestParam String message) {

        try {
            return openAiService.talkToAi(message);

        } catch (Exception e) {

            e.printStackTrace();

            return "AI ERROR:\n"
                    + e.getClass().getName()
                    + "\n\nMESSAGE:\n"
                    + e.getMessage();
        }
    }
}