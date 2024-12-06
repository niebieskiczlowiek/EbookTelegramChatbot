package com.technischools.telegramApp.chatApi;

import com.technischools.telegramApp.pdfReader.PDFReader;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ChatAPIService {
    private OpenAiService openAiService;
    private String model;
    private final List<ChatMessage> chatMessages = new ArrayList<>();

    @Value("${openai.api.key:OPENAI_API_KEY}")
    String apiKey;

    @PostConstruct
    public void init() {
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(30));
        this.model = "gpt-4o";
        prepareChat();
    }

    private void prepareChat() {
        String text = this.getLearningText();
        String initMessage = """
                Na podstawie tekstu odpowiadaj na pytania\
                i udzielaj porad.\
                Gdy zostaniesz zapytany o coś spoza zakresu tekstu,\
                wytłumacz, że nie posiadasz takiej wiedzy.\
                Nie mów o tekście. Udawaj, że to twoja wiedza.\
                Zachowaj profesjonalny ton.\
                Przedstaw się jako asystent.\
                Staraj się odpowiadać w miare krótko\
                
                Tekst:
                ```""" +
                text
                + """
                ```
                """;

        ChatMessage systemMessage = new ChatMessage("system", initMessage);
        addNewMessage(systemMessage);
    }

    private String getLearningText() {
        String commonPath = "/home/lenovo/Desktop/Java/EbookChatbot/ebookTelegramChatbot/src/main/resources/pdf/";
        List<String> paths = PDFReader.getFilesFromPath(commonPath);
        assert paths != null;
        StringBuilder pdfsText = new StringBuilder();
        paths.forEach(p -> {
            try {
                String pdfText = PDFReader.getTextFromPDF(commonPath + p);
                pdfsText.append(pdfText).append("\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return pdfsText.toString();
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
                .maxTokens(200)
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
