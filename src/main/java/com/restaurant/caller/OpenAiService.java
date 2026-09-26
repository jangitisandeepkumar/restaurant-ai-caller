package com.restaurant.caller;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.restaurant.caller.service.AiConversationService;
import com.restaurant.caller.service.ConversationSession;

@Service
public class OpenAiService {

    private final Client client;
    private final AiConversationService aiConversationService;
    private final ConversationSession conversationSession;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public OpenAiService(
            AiConversationService aiConversationService,
            ConversationSession conversationSession) {

        this.aiConversationService = aiConversationService;
        this.conversationSession = conversationSession;

        String apiKey = System.getenv("GOOGLE_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "GOOGLE_API_KEY is not available to Java."
            );
        }

        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    // =========================================================
    // 1. NORMAL AI CONVERSATION
    // =========================================================

    public String talkToAi(String managerMessage) {

        // Save manager's latest message
        conversationSession.addManagerMessage(managerMessage);

        // Get our AI instructions
        String instructions =
                aiConversationService.createSystemInstructions();

        // Get complete conversation so far
        String conversation =
                conversationSession.getConversation();

        // Create complete prompt
        String fullPrompt =
                instructions
                + "\n\n"
                + "CURRENT CONVERSATION:\n"
                + conversation
                + "\n\n"
                + "Respond naturally to the manager's latest message.";

        // Models to try
        String[] modelsToTry = {

            "gemini-3.8-flash",
            "gemini-3.7-flash",
            "gemini-3.5-flash",
            "gemini-3.5-flash-lite",

            "gemini-2.5-flash",
            "gemini-2.0-flash",
            "gemini-1.5-flash",
            "gemini-1.5-pro"
        };

        // Try each model
        for (String modelName : modelsToTry) {

            try {

                System.out.println(
                        "Trying model: " + modelName + "..."
                );

                GenerateContentResponse response =
                        client.models.generateContent(
                                modelName,
                                fullPrompt,
                                null
                        );

                String aiResponse =
                        response.text();

                // Save AI response into conversation
                conversationSession.addAiMessage(aiResponse);

                System.out.println(
                        "Success with model: "
                        + modelName
                );

                return aiResponse;

            } catch (Exception e) {

                System.err.println(
                        "Skipped "
                        + modelName
                        + " due to error: "
                        + e.getMessage()
                );
            }
        }

        return "AI temporarily unavailable.";
    }

    // =========================================================
    // 2. EXTRACT FINAL CALL RESULT
    // =========================================================

    public String extractCallResult(String conversation) {

        String prompt =

                "You are a system that extracts the final result "
                + "from a restaurant promotion phone conversation.\n\n"

                + "Analyze the conversation carefully.\n\n"

                + "Return ONLY valid JSON.\n"
                + "Do NOT use markdown.\n"
                + "Do NOT use ```.\n"
                + "Do NOT add explanations before or after the JSON.\n\n"

                // -------------------------------------------------
                // REQUIRED JSON FORMAT
                // -------------------------------------------------

                + "Return exactly this structure:\n"

                + "{\n"
                + "  \"promotion\": \"Yes/No/Maybe\",\n"
                + "  \"payment\": \"amount or empty string\",\n"
                + "  \"ifYesDay\": \"day/date or empty string\",\n"
                + "  \"status\": \"Confirmed/Not Interested/Call Back/No Answer\"\n"
                + "}\n\n"

                // -------------------------------------------------
                // EXTRACTION RULES
                // -------------------------------------------------

                + "RULES:\n\n"

                // PROMOTION
                + "1. PROMOTION\n"
                + "- Use \"Yes\" only when the restaurant manager "
                + "clearly agrees to the promotion.\n"

                + "- Use \"No\" when the manager clearly refuses "
                + "the promotion.\n"

                + "- Use \"Maybe\" when the manager shows interest "
                + "but has not clearly agreed or refused.\n\n"

                // PAYMENT
                + "2. PAYMENT\n"
                + "- Put the agreed payment amount here.\n"

                + "- If the manager agrees to the standard price "
                + "of ₹500, return \"500\".\n"

                + "- If another amount is explicitly agreed, "
                + "return that amount.\n"

                + "- If no payment amount was agreed, "
                + "return an empty string.\n\n"

                // DAY
                + "3. IF YES DAY\n"
                + "- If the manager agrees and a day or date "
                + "is confirmed, return that day or date.\n"

                + "- Examples: \"Monday\", \"Saturday\", "
                + "\"October 3\", \"Tomorrow\".\n"

                + "- If no day or date is confirmed, "
                + "return an empty string.\n\n"

                // STATUS
                + "4. STATUS\n"

                + "- Use \"Confirmed\" when the promotion is clearly "
                + "accepted and the agreement is confirmed.\n"

                + "- Use \"Not Interested\" when the manager clearly "
                + "refuses the promotion.\n"

                + "- Use \"Call Back\" when the manager asks to be "
                + "contacted again later.\n"

                + "- Use \"No Answer\" when there is no meaningful "
                + "manager response or the call does not result "
                + "in a conversation.\n\n"

                // DO NOT INVENT
                + "5. DO NOT INVENT INFORMATION\n"

                + "- Never guess a payment amount.\n"
                + "- Never guess a day or date.\n"

                + "- Never assume agreement when the manager has "
                + "not clearly agreed.\n"

                + "- Never assume refusal when the manager is "
                + "only asking questions.\n\n"

                // CONVERSATION
                + "CONVERSATION:\n"
                + conversation;

        // =========================================================
        // MODELS
        // =========================================================

        String[] modelsToTry = {

            "gemini-3.8-flash",
            "gemini-3.7-flash",
            "gemini-3.5-flash",
            "gemini-3.5-flash-lite",

            "gemini-2.5-flash",
            "gemini-2.0-flash",
            "gemini-1.5-flash",
            "gemini-1.5-pro"
        };

        // =========================================================
        // TRY MODELS
        // =========================================================

        for (String modelName : modelsToTry) {

            try {

                System.out.println(
                        "Trying result extraction model: "
                        + modelName
                        + "..."
                );

                GenerateContentResponse response =
                        client.models.generateContent(
                                modelName,
                                prompt,
                                null
                        );

                System.out.println(
                        "Result extraction successful with: "
                        + modelName
                );

                return response.text();

            } catch (Exception e) {

                System.err.println(
                        "Result extraction failed with "
                        + modelName
                        + ": "
                        + e.getMessage()
                );
            }
        }

        // =========================================================
        // FALLBACK
        // =========================================================

        return "{"
                + "\"promotion\":\"Maybe\","
                + "\"payment\":\"\","
                + "\"ifYesDay\":\"\","
                + "\"status\":\"No Answer\""
                + "}";
    }
}