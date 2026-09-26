package com.restaurant.caller.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class ExotelService {

    // ==========================================
    // EXOTEL ACCOUNT DETAILS
    // ==========================================

    private static final String ACCOUNT_SID =
            "student1794";

    /*
     * Paste your API Key between the quotes.
     */
    private static final String API_KEY =
            "4c96cac5acc777f9dc3b8cf589e374e5076cc5dcf822d564";

    /*
     * Paste your API Token between the quotes.
     */
    private static final String API_TOKEN =
            "1e6b9099be50d248fdc4eefeeaeda35de335dc78d3f5425a";


    // ==========================================
    // EXOTEL API URL
    // ==========================================

    private static final String EXOTEL_URL =
            "https://api.exotel.com/v1/Accounts/"
            + ACCOUNT_SID
            + "/Calls/connect";


    // ==========================================
    // HTTP CLIENT
    // ==========================================

    private final HttpClient httpClient =
            HttpClient.newHttpClient();


    // ==========================================
    // MAKE CALL
    // ==========================================

    public String makeCall(
            String fromNumber,
            String toNumber,
            String exoPhone)
            throws Exception {

        // --------------------------------------
        // Check API credentials
        // --------------------------------------

        if (API_KEY == null
                || API_KEY.isBlank()
                || API_KEY.equals(
                        "4c96cac5acc777f9dc3b8cf589e374e5076cc5dcf822d564E")) {

            throw new IllegalStateException(
                    "Exotel API Key is missing."
            );
        }

        if (API_TOKEN == null
                || API_TOKEN.isBlank()
                || API_TOKEN.equals(
                        "4c96cac5acc777f9dc3b8cf589e374e5076cc5dcf822d564")) {

            throw new IllegalStateException(
                    "Exotel API Token is missing."
            );
        }


        // --------------------------------------
        // Clean phone numbers
        // --------------------------------------

        fromNumber =
                cleanNumber(fromNumber);

        toNumber =
                cleanNumber(toNumber);

        exoPhone =
                cleanNumber(exoPhone);


        // --------------------------------------
        // Create Basic Authentication
        // --------------------------------------

        String authentication =
                API_KEY + ":" + API_TOKEN;

        String encodedAuthentication =
                Base64.getEncoder()
                        .encodeToString(
                                authentication.getBytes(
                                        StandardCharsets.UTF_8
                                )
                        );


        // --------------------------------------
        // Create form data
        // --------------------------------------

        String formData =
                "From="
                + encode(fromNumber)

                + "&To="
                + encode(toNumber)

                + "&CallerId="
                + encode(exoPhone);


        // --------------------------------------
        // Create HTTP request
        // --------------------------------------

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        EXOTEL_URL
                                )
                        )
                        .header(
                                "Authorization",
                                "Basic "
                                        + encodedAuthentication
                        )
                        .header(
                                "Content-Type",
                                "application/x-www-form-urlencoded"
                        )
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofString(formData)
                        )
                        .build();


        // --------------------------------------
        // Send request
        // --------------------------------------

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers
                                .ofString()
                );


        // --------------------------------------
        // Print response
        // --------------------------------------

        System.out.println(
                "========================================"
        );

        System.out.println(
                "EXOTEL HTTP STATUS: "
                        + response.statusCode()
        );

        System.out.println(
                "EXOTEL RESPONSE:"
        );

        System.out.println(
                response.body()
        );

        System.out.println(
                "========================================"
        );


        // --------------------------------------
        // Return response
        // --------------------------------------

        return response.body();
    }


    // ==========================================
    // CLEAN PHONE NUMBER
    // ==========================================

    private String cleanNumber(
            String number) {

        if (number == null
                || number.isBlank()) {

            throw new IllegalArgumentException(
                    "Phone number cannot be empty."
            );
        }

        return number
                .trim()
                .replace(" ", "")
                .replace("-", "");
    }


    // ==========================================
    // URL ENCODE
    // ==========================================

    private String encode(
            String value) {

        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
    }
}