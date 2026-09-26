package com.restaurant.caller.service;

import org.springframework.stereotype.Service;

@Service
public class AiConversationService {

   public String createSystemInstructions() {

    String instructions =
            ChannelConfig.AI_BEHAVIOR
            + "\n\n"
            + "OPENING MESSAGE:\n"
            + ChannelConfig.OPENING_MESSAGE
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
            + "SALES GOAL: "
            + ChannelConfig.SALES_GOAL;

    return instructions;
}
}