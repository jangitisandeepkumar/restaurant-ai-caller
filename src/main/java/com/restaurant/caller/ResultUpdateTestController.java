package com.restaurant.caller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.caller.service.GoogleSheetsService;

@RestController
@RequestMapping("/result")
public class ResultUpdateTestController {

    private final GoogleSheetsService googleSheetsService;
    private final ObjectMapper objectMapper;

    public ResultUpdateTestController(
            GoogleSheetsService googleSheetsService,
            ObjectMapper objectMapper) {

        this.googleSheetsService = googleSheetsService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/update")
    public String updateResult(
            @RequestParam int rowNumber,
            @RequestParam String json) {

        try {

            // Convert JSON → CallResult object
            CallResult result =
                    objectMapper.readValue(json, CallResult.class);

            // Update Google Sheet
            googleSheetsService.updateRestaurantResult(
                    rowNumber,
                    result.getPromotion(),
                    result.getPayment(),
                    result.getIfYesDay(),
                    result.getStatus()
            );

            return "Google Sheet updated successfully!";

        } catch (Exception e) {

            e.printStackTrace();

            return "UPDATE ERROR:\n"
                    + e.getClass().getName()
                    + "\n\n"
                    + e.getMessage();
        }
    }
}