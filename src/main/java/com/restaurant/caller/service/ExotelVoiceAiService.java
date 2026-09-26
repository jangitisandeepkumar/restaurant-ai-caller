package com.restaurant.caller.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.client.RestClient;

@Service
public class ExotelVoiceAiService {

    public String startCall(
            String restaurantPhone,
            String publicWebSocketUrl) {

        String apiKey =
                System.getenv(
                        "EXOTEL_API_KEY"
                );

        String apiToken =
                System.getenv(
                        "EXOTEL_API_TOKEN"
                );

        String accountSid =
                System.getenv(
                        "EXOTEL_ACCOUNT_SID"
                );

        String callerId =
                System.getenv(
                        "EXOTEL_CALLER_ID"
                );

        String apiBase =
                System.getenv(
                        "EXOTEL_API_BASE"
                );

        if (apiKey == null
                || apiKey.isBlank()
                || apiToken == null
                || apiToken.isBlank()
                || accountSid == null
                || accountSid.isBlank()
                || callerId == null
                || callerId.isBlank()
                || apiBase == null
                || apiBase.isBlank()) {

            throw new IllegalStateException(
                    "Exotel environment variables are missing."
            );
        }

        /*
         * Gemini input = 16 kHz.
         * Exotel supports 16 kHz streams.
         */

        String streamUrl =
                publicWebSocketUrl
                + "?sample-rate=16000";

        MultiValueMap<String, String> form =
                new LinkedMultiValueMap<>();

        form.add(
                "From",
                restaurantPhone
        );

        form.add(
                "CallerId",
                callerId
        );

        form.add(
                "StreamUrl",
                streamUrl
        );

        form.add(
                "StreamType",
                "bidirectional"
        );

        form.add(
                "Record",
                "false"
        );

        form.add(
                "TimeLimit",
                "600"
        );

        RestClient client =
                RestClient.builder()
                        .baseUrl(apiBase)
                        .defaultHeaders(
                                headers ->
                                        headers.setBasicAuth(
                                                apiKey,
                                                apiToken
                                        )
                        )
                        .build();

        return client.post()
                .uri(
                        "/v1/Accounts/"
                        + accountSid
                        + "/Calls/connect"
                )
                .contentType(
                        MediaType.MULTIPART_FORM_DATA
                )
                .body(form)
                .retrieve()
                .body(String.class);
    }
}