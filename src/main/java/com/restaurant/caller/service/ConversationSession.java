package com.restaurant.caller.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ConversationSession {

    private final List<String> messages = new ArrayList<>();

    public void addManagerMessage(String message) {
        messages.add("MANAGER: " + message);
    }

    public void addAiMessage(String message) {
        messages.add("AI: " + message);
    }

    public String getConversation() {
        return String.join("\n", messages);
    }

    public void clear() {
        messages.clear();
    }
}