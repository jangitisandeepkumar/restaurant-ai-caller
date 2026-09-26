package com.restaurant.caller.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.caller.CallResult;
import com.restaurant.caller.OpenAiService;

@Service
public class CallResultService {

    private final OpenAiService openAiService;
    private final GoogleSheetsService googleSheetsService;
    private final ObjectMapper objectMapper;

    public CallResultService(
            OpenAiService openAiService,
            GoogleSheetsService googleSheetsService,
            ObjectMapper objectMapper) {

        this.openAiService = openAiService;
        this.googleSheetsService = googleSheetsService;
        this.objectMapper = objectMapper;
    }

    public CallResult processCallResult(
            int rowNumber,
            String conversation) throws Exception {

        // Step 1: Ask Gemini to extract the result
        String json =
                openAiService.extractCallResult(conversation);

        System.out.println("AI RESULT:");
        System.out.println(json);

        // Step 2: Convert JSON into CallResult
        CallResult result =
                objectMapper.readValue(json, CallResult.class);

        // Step 3: Update Google Sheet
        googleSheetsService.updateRestaurantResult(
                rowNumber,
                result.getPromotion(),
                result.getPayment(),
                result.getIfYesDay(),
                result.getStatus()
        );

        System.out.println("Google Sheet updated.");

        return result;
    }
}
