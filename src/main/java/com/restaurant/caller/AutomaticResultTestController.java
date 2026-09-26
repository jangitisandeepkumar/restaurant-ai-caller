package com.restaurant.caller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.caller.service.CallResultService;

@RestController
@RequestMapping("/call")
public class AutomaticResultTestController {

    private final CallResultService callResultService;

    public AutomaticResultTestController(
            CallResultService callResultService) {

        this.callResultService = callResultService;
    }

    @GetMapping("/process-result")
    public Object processResult(
            @RequestParam int rowNumber,
            @RequestParam String conversation) {

        try {

            return callResultService.processCallResult(
                    rowNumber,
                    conversation
            );

        } catch (Exception e) {

            e.printStackTrace();

            return "ERROR:\n"
                    + e.getClass().getName()
                    + "\n\n"
                    + e.getMessage();
        }
    }
}