package com.technischools.telegramApp.bot;

import com.technischools.telegramApp.chatapi.ChatAPIController;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;

@Service
public class BotService {
    private final ChatAPIController chatApiController;

    @Autowired
    public BotService(ChatAPIController chatApiController) {
        this.chatApiController = chatApiController;
    }

    public String getBotResponse(String userMessage) {
        ChatCompletionResult chatResponse = this.chatApiController.getChatResponseFromPrompt(userMessage);
        ChatMessage receivedMessage = this.chatApiController.getMessageFromResponse(chatResponse);
        return this.chatApiController.getMessageContent(receivedMessage);
    }
}
