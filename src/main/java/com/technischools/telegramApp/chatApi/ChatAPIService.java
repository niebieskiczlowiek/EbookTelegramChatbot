package com.technischools.telegramApp.chatApi;

import com.technischools.telegramApp.utils.PDFReader;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/*
* Class that handles all OpenAi API operations, such as:
* - setting up and OpenAi chatbot
* - generating text from prompts
* - preparing prompts for execution
* - retrieving necessary PDF training data
* */
@Slf4j
@Service
public class ChatAPIService {
    private OpenAiService openAiService;
    private String model;
    private final List<ChatMessage> chatMessages = new ArrayList<>();
    private final String commonPath = "/home/lenovo/Desktop/Java/EbookChatbot/ebookTelegramChatbot/src/main/resources/pdf/";

    @Value("${openai.api.key:OPENAI_API_KEY}")
    String apiKey;

    @PostConstruct
    public void init() throws IOException {
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(30));
        this.model = "gpt-4o";
        prepareChat();
    }

    /*
    * Prepares the chat for conversation
    * Uses training data and custom prompts to make sure chat works properly
    * */
    private void prepareChat() throws IOException {
//        String text = this.getLearningText("");
//        System.out.println(text);
//        String initMessage = """
//                Na podstawie tekstu odpowiadaj na pytania\
//                i udzielaj porad.\
//                Gdy zostaniesz zapytany o coś spoza zakresu tekstu,\
//                wytłumacz, że nie posiadasz takiej wiedzy.\
//                Nie mów o tekście. Udawaj, że to twoja wiedza.\
//                Zachowaj profesjonalny ton.\
//                Przedstaw się jako asystent.\
//                Staraj się odpowiadać w miare krótko\
//
//                Tekst:
//                ```""" +
//                text
//                + """
//                ```
//                """;
        String initMessage = """
                Jesteś asystentem w kwestiach diety i odżywiania.\
                Będziesz otrzymywał zapytania oraz tekst, który może zawierać na nie odpowiedź.\
                Na podstawie tekstu udzielaj odpowiedzi i porad.\
                Gdy zostaniesz zapytany o coś spoza zakresu tekstu,\
                wytłumacz, że nie posiadasz takiej wiedzy.\
                Nie mów o tekście. Udawaj, że to twoja wiedza.\
                """;

        ChatMessage systemMessage = new ChatMessage("system", initMessage);
        addNewMessage(systemMessage);
    }

//    private String getLearningText() {
//        List<String> paths = PDFReader.getFilesFromPath(this.commonPath);
//        assert paths != null;
//        StringBuilder pdfsText = new StringBuilder();
//        paths.forEach(p -> {
//            try {
//                String pdfText = PDFReader.getTextFromPDF(this.commonPath + p);
//                pdfsText.append(pdfText).append("\n");
//            } catch (IOException e) {
//                log.error("e: ", e);
//            }
//        });
//        return pdfsText.toString();
//    }
    private String getLearningText(String prompt) throws IOException {
        // gets all pages of all pdfs available
        List<PDDocument> files = PDFReader.getFilesFromPath(this.commonPath);
        assert files != null;
        List<PDDocument> pdfsPages = PDFReader.splitIntoPages(files);

        // finds relevant pages based on user prompt
        List<PDDocument> relevantPages = PDFReader.findRelevantPages(pdfsPages, prompt);
        relevantPages = relevantPages.subList(0, Math.min(5, relevantPages.size()));

        // retrieves text from the relevant pages
        StringBuilder pdfsText = new StringBuilder();
        relevantPages.forEach(p -> {
            try {
                String pdfText = PDFReader.getTextFromPDF(p);
                pdfsText.append(pdfText).append("\n");
            } catch (IOException e) {
                log.error("e: ", e);
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
    public ChatCompletionResult getChatResponse(String userPrompt) throws IOException {
        String learningText = this.getLearningText(userPrompt);

        String prompt = """
                Na podstawie tego tekstu odpowiedz na zadane ci pytanie\
                Jesli zostaniesz o to poproszony, udziel też porad\
                
                Pytanie:
                ```
                """ +
                userPrompt
                + """
                ```
                
                Tekst:
                ```
                """ +
                learningText
                + """
                ```
                """;

        ChatMessage userChatMessage = this.preparePrompt(prompt);
        return generateChatCompletion(userChatMessage);
    }
}
