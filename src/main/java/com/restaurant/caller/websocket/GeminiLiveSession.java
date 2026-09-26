package com.restaurant.caller.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.caller.service.ChannelConfig;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class GeminiLiveSession {

    private final WebSocketSession exotelSession;
    private final ObjectMapper objectMapper;

    private WebSocket geminiSocket;

    private String streamSid;

    private boolean geminiConnected = false;

    public GeminiLiveSession(
            WebSocketSession exotelSession,
            ObjectMapper objectMapper) {

        this.exotelSession = exotelSession;
        this.objectMapper = objectMapper;
    }

    public void setStreamSid(String streamSid) {

        this.streamSid = streamSid;

        System.out.println(
                "STREAM SID SET: " + streamSid
        );

        sendOpeningMessage();
    }

    public void connect() {

        String apiKey =
                System.getenv("GOOGLE_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {

            System.err.println(
                    "GOOGLE_API_KEY is missing."
            );

            return;
        }

        String url =
                "wss://generativelanguage.googleapis.com/ws/"
                + "google.ai.generativelanguage.v1beta."
                + "GenerativeService.BidiGenerateContent"
                + "?key="
                + apiKey;

        System.out.println(
                "Connecting to Gemini Live..."
        );

        HttpClient client =
                HttpClient.newHttpClient();

        client.newWebSocketBuilder()
                .buildAsync(
                        URI.create(url),
                        new GeminiListener()
                )
                .thenAccept(socket -> {

                    geminiSocket = socket;
                    geminiConnected = true;

                    System.out.println(
                            "GEMINI LIVE CONNECTED"
                    );

                    sendSetup();
                })
                .exceptionally(error -> {

                    geminiConnected = false;

                    System.err.println(
                            "GEMINI CONNECTION ERROR: "
                            + error.getMessage()
                    );

                    return null;
                });
    }

    private void sendSetup() {

        String systemInstruction =
                ChannelConfig.AI_BEHAVIOR
                + "\n\n"
                + "CHANNEL NAME: "
                + ChannelConfig.CHANNEL_NAME
                + "\n"
                + "CHANNEL DESCRIPTION: "
                + ChannelConfig.CHANNEL_DESCRIPTION
                + "\n"
                + "PROMOTION: "
                + ChannelConfig.PROMOTION_DESCRIPTION
                + "\n"
                + "PROMOTION PRICE: ₹"
                + ChannelConfig.PROMOTION_PRICE
                + "\n\n"
                + "OPENING MESSAGE:\n"
                + ChannelConfig.OPENING_MESSAGE;

        try {

            String json =
                    "{"
                    + "\"setup\":{"
                    + "\"model\":\"models/gemini-3.8-live\","
                    + "\"generationConfig\":{"
                    + "\"responseModalities\":[\"AUDIO\"],"
                    + "\"speechConfig\":{"
                    + "\"voiceConfig\":{"
                    + "\"prebuiltVoiceConfig\":{"
                    + "\"voiceName\":\"Puck\""
                    + "}"
                    + "}"
                    + "}"
                    + "},"
                    + "\"systemInstruction\":{"
                    + "\"parts\":[{"
                    + "\"text\":"
                    + objectMapper
                            .valueToTree(systemInstruction)
                            .toString()
                    + "}]"
                    + "}"
                    + "}"
                    + "}";

            geminiSocket.sendText(
                    json,
                    true
            );

            System.out.println(
                    "GEMINI SETUP SENT"
            );

        } catch (Exception e) {

            System.err.println(
                    "GEMINI SETUP ERROR: "
                    + e.getMessage()
            );
        }
    }

    public void sendOpeningMessage() {

        if (!geminiConnected
                || geminiSocket == null) {

            System.out.println(
                    "Gemini not ready yet. "
                    + "Opening message will be sent when connected."
            );

            return;
        }

        try {

            String text =
                    "The restaurant manager has answered "
                    + "the phone. Start the conversation now. "
                    + "Speak the configured opening message "
                    + "naturally and then wait for the manager "
                    + "to respond. Do not repeat the opening "
                    + "message later.";

            String json =
                    "{"
                    + "\"clientContent\":{"
                    + "\"turns\":[{"
                    + "\"role\":\"user\","
                    + "\"parts\":[{"
                    + "\"text\":"
                    + objectMapper
                            .valueToTree(text)
                            .toString()
                    + "}]"
                    + "}],"
                    + "\"turnComplete\":true"
                    + "}"
                    + "}";

            geminiSocket.sendText(
                    json,
                    true
            );

            System.out.println(
                    "OPENING MESSAGE REQUEST SENT"
            );

        } catch (Exception e) {

            System.err.println(
                    "OPENING MESSAGE ERROR: "
                    + e.getMessage()
            );
        }
    }

    public void sendAudio(String base64Audio) {

        if (!geminiConnected
                || geminiSocket == null) {

            return;
        }

        try {

            String json =
                    "{"
                    + "\"realtimeInput\":{"
                    + "\"audio\":{"
                    + "\"data\":"
                    + objectMapper
                            .valueToTree(base64Audio)
                            .toString()
                    + ","
                    + "\"mimeType\":\"audio/pcm;rate=16000\""
                    + "}"
                    + "}"
                    + "}";

            geminiSocket.sendText(
                    json,
                    true
            );

        } catch (Exception e) {

            System.err.println(
                    "GEMINI AUDIO SEND ERROR: "
                    + e.getMessage()
            );
        }
    }

    private class GeminiListener
            implements WebSocket.Listener {

        @Override
        public void onOpen(
                WebSocket webSocket) {

            System.out.println(
                    "GEMINI WEBSOCKET OPENED"
            );

            WebSocket.Listener.super.onOpen(
                    webSocket
            );
        }

        @Override
        public CompletionStage<?> onText(
                WebSocket webSocket,
                CharSequence data,
                boolean last) {

            try {

                JsonNode root =
                        objectMapper.readTree(
                                data.toString()
                        );

                if (root.has("setupComplete")) {

                    System.out.println(
                            "GEMINI SETUP COMPLETE"
                    );

                    sendOpeningMessage();
                }

                JsonNode serverContent =
                        root.path("serverContent");

                JsonNode modelTurn =
                        serverContent.path("modelTurn");

                JsonNode parts =
                        modelTurn.path("parts");

                if (parts.isArray()) {

                    for (JsonNode part : parts) {

                        JsonNode inlineData =
                                part.path("inlineData");

                        if (!inlineData.isMissingNode()) {

                            String audio =
                                    inlineData
                                            .path("data")
                                            .asText();

                            if (!audio.isBlank()) {

                                sendAudioToExotel(
                                        audio
                                );
                            }
                        }
                    }
                }

                JsonNode inputTranscription =
                        serverContent
                                .path("inputTranscription");

                if (!inputTranscription
                        .isMissingNode()) {

                    String text =
                            inputTranscription
                                    .path("text")
                                    .asText();

                    if (!text.isBlank()) {

                        System.out.println(
                                "MANAGER: " + text
                        );
                    }
                }

                JsonNode outputTranscription =
                        serverContent
                                .path("outputTranscription");

                if (!outputTranscription
                        .isMissingNode()) {

                    String text =
                            outputTranscription
                                    .path("text")
                                    .asText();

                    if (!text.isBlank()) {

                        System.out.println(
                                "AI: " + text
                        );
                    }
                }

            } catch (Exception e) {

                System.err.println(
                        "GEMINI RESPONSE ERROR: "
                        + e.getMessage()
                );
            }

            return WebSocket.Listener.super.onText(
                    webSocket,
                    data,
                    last
            );
        }

        @Override
        public void onError(
                WebSocket webSocket,
                Throwable error) {

            geminiConnected = false;

            System.err.println(
                    "GEMINI WEBSOCKET ERROR: "
                    + error.getMessage()
            );
        }

        @Override
        public CompletionStage<?> onClose(
                WebSocket webSocket,
                int statusCode,
                String reason) {

            geminiConnected = false;

            System.out.println(
                    "GEMINI CLOSED: "
                    + statusCode
                    + " - "
                    + reason
            );

            return WebSocket.Listener.super.onClose(
                    webSocket,
                    statusCode,
                    reason
            );
        }
    }

    private void sendAudioToExotel(
            String audioBase64) {

        try {

            if (exotelSession == null
                    || !exotelSession.isOpen()) {

                return;
            }

            if (streamSid == null
                    || streamSid.isBlank()) {

                return;
            }

            /*
             * Gemini output is 24 kHz PCM.
             * Exotel supports 24 kHz bidirectional audio,
             * so we use 24 kHz for this stream.
             */

            String json =
                    "{"
                    + "\"event\":\"media\","
                    + "\"stream_sid\":"
                    + objectMapper
                            .valueToTree(streamSid)
                            .toString()
                    + ","
                    + "\"media\":{"
                    + "\"payload\":"
                    + objectMapper
                            .valueToTree(audioBase64)
                            .toString()
                    + "}"
                    + "}";

            exotelSession.sendMessage(
                    new TextMessage(json)
            );

        } catch (Exception e) {

            System.err.println(
                    "EXOTEL AUDIO SEND ERROR: "
                    + e.getMessage()
            );
        }
    }

    public void close() {

        geminiConnected = false;

        if (geminiSocket != null) {

            try {

                geminiSocket.sendClose(
                        WebSocket.NORMAL_CLOSURE,
                        "Call ended"
                );

            } catch (Exception e) {

                System.err.println(
                        "Gemini close error: "
                        + e.getMessage()
                );
            }
        }
    }
}