package com.restaurant.caller.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CallWebSocketHandler
        extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    private final Map<String, GeminiLiveSession>
            sessions =
            new ConcurrentHashMap<>();

    public CallWebSocketHandler(
            ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(
            WebSocketSession session) {

        System.out.println(
                "EXOTEL CONNECTED: "
                + session.getId()
        );

        GeminiLiveSession geminiLiveSession =
                new GeminiLiveSession(
                        session,
                        objectMapper
                );

        sessions.put(
                session.getId(),
                geminiLiveSession
        );

        geminiLiveSession.connect();
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message) {

        try {

            JsonNode root =
                    objectMapper.readTree(
                            message.getPayload()
                    );

            String event =
                    root.path("event").asText();

            System.out.println(
                    "EXOTEL EVENT: " + event
            );

            GeminiLiveSession gemini =
                    sessions.get(
                            session.getId()
                    );

            if ("connected".equals(event)) {

                System.out.println(
                        "EXOTEL STREAM CONNECTED"
                );
            }

            else if ("start".equals(event)) {

                String streamSid =
                        root.path("start")
                                .path("stream_sid")
                                .asText();

                if (streamSid.isBlank()) {

                    streamSid =
                            root.path("stream_sid")
                                    .asText();
                }

                System.out.println(
                        "STREAM SID: "
                        + streamSid
                );

                if (gemini != null) {

                    gemini.setStreamSid(
                            streamSid
                    );
                }
            }

            else if ("media".equals(event)) {

                String payload =
                        root.path("media")
                                .path("payload")
                                .asText();

                if (gemini != null
                        && !payload.isBlank()) {

                    gemini.sendAudio(
                            payload
                    );
                }
            }

            else if ("dtmf".equals(event)) {

                String digit =
                        root.path("dtmf")
                                .path("digit")
                                .asText();

                System.out.println(
                        "DTMF: " + digit
                );
            }

            else if ("mark".equals(event)) {

                System.out.println(
                        "EXOTEL MARK EVENT"
                );
            }

            else if ("stop".equals(event)) {

                System.out.println(
                        "EXOTEL CALL STOPPED"
                );

                removeSession(
                        session.getId()
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "EXOTEL WEBSOCKET ERROR: "
                    + e.getMessage()
            );
        }
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            org.springframework.web.socket.CloseStatus status) {

        System.out.println(
                "EXOTEL DISCONNECTED: "
                + session.getId()
        );

        removeSession(
                session.getId()
        );
    }

    private void removeSession(
            String sessionId) {

        GeminiLiveSession gemini =
                sessions.remove(
                        sessionId
                );

        if (gemini != null) {

            gemini.close();
        }
    }
}