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
        this.openAiService = new OpenAiService(apiKey);
        this.model = "gpt-4-turbo";
        prepareChat();
    }

    private void prepareChat() {
        String text = this.getLearningText();
        String initMessage = """
                Na podstawie tekstu załączonego poniżej odpowiadaj na pytania\
                i udzielaj porad.\
                Nie odpowiadaj na pytania, które nie są związane z tekstem.\
                Gdy zostaniesz zapytany o coś spoza zakresu tekstu,\
                wytłumacz, że nie posiadasz takiej wiedzy.\
                
                Nie wspominaj o przekazanym ci tekście. Udawaj, że jest to twoja własna wiedza.\
                Zachowaj profesjonalny ton.\
                
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
                .maxTokens(500)
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