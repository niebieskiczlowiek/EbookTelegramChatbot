package com.technischools.telegramApp.chatapi;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.utils.TikTokensUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatAPIService {
    private OpenAiService openAiService;
    private String model;
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @Value("${openai.api.key:OPENAI_API_KEY}")
    String apiKey;

    @PostConstruct
    public void init() {
        this.openAiService = new OpenAiService(apiKey);
//        this.model = TikTokensUtil.ModelEnum.GPT_4.getName();
        this.model = "gpt-4o";

        ChatMessage systemMessage = new ChatMessage("system", "");
        addNewMessage(systemMessage);
    }

    /*
    * adds new message to message stack
    * */
    private void addNewMessage(ChatMessage message) {
        chatMessages.add(message);
    }

    /*
    * prepares new chat message object based on given prompt
    * */
    private ChatMessage preparePrompt(String prompt) {
        return new ChatMessage("user", prompt);
    }

    /*
    * generates a result object based on given chat message
    * */
    private ChatCompletionResult generateChatCompletion(ChatMessage chatMessage) {
        this.addNewMessage(chatMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model(model)
                .maxTokens(100)
                .messages(chatMessages)
                .build();
        return this.openAiService.createChatCompletion(chatCompletionRequest);
    }

    /*
    * returns a generated result based on users prompt
    * */
    public ChatCompletionResult getChatResponse(String userPrompt) {
        ChatMessage userChatMessage = this.preparePrompt(userPrompt);
        return generateChatCompletion(userChatMessage);
    }
}
