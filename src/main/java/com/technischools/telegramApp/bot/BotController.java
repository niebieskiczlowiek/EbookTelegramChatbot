package com.technischools.telegramApp.bot;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;

/*
* Controller class for handling a basic Telegram chatbot,
* to allow the user to talk to the Chat Api
* */
@Component
public class BotController implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private TelegramClient telegramClient;
    private final BotService botService;

    @Value("${telegram.bot.token:TELEGRAM_BOT_TOKEN}")
    private String botToken;

    @Value("${telegram.bot.name:TELEGRAM_BOT_NAME}")
    private String botName;

    @Autowired
    public BotController(BotService botService) {
        this.botService = botService;
    }

    @PostConstruct
    public void init() {
        this.telegramClient = new OkHttpTelegramClient(this.getBotToken());
    } // we have to use @PostConstruct instead of constructor to use the env variables

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    /*
    * Main method which retrieves text messages sent to the chatbot,
    * gets a response from the ChatAPI based on the given text,
    * and returns the API's response
    * */
    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = null;
            try {
                message_text = this.botService.getBotResponse(update.getMessage().getText());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            long chat_id = update.getMessage().getChatId();

            SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text(message_text)
                    .build();

            try {
                this.telegramClient.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
