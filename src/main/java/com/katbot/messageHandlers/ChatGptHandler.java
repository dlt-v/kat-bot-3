package com.katbot.messageHandlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Component
public class ChatGptHandler implements Handler {

    private static final String USED_MODEL = "gpt-3.5-turbo";
    private static final String OPENAI_API_KEY = System.getenv("openai-token");
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final Logger logger = LoggerFactory.getLogger(ChatGptHandler.class);

    @Override
    public void handle(MessageReceivedEvent event) {
        String userMessage = event.getAuthor().getName() + " wrote: " + event.getMessage().getContentDisplay().substring(4).trim();
        // call the GPT-3 API
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + OPENAI_API_KEY);
        headers.set("Content-Type", "application/json");

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", USED_MODEL);
        List<Map<String, String>> messages = new ArrayList<>();

        String katBotInstructions = "You are KatBot, an edgy and sarcastic Khajiit Discord bot. Delta (or delta.v) is your creator. " +
                "You have a personality of a worker who isn't paid enough for this crap. " +
                "Write in first person. You are a furry, your users are furries and play VRChat." +
                "Your responses are dry, short and cynical, you need to have an opinion about everything. " +
                "If you are faced with a choice, you must pick one or the other. " +
                "Write one or two simple sentences at most.";

        messages.add(Map.of("role", "system", "content", katBotInstructions));
        messages.add(Map.of("role", "user", "content", userMessage));

        payload.put("messages", messages);
        logger.info("User asked KatBot: {}", userMessage);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(OPENAI_API_URL, HttpMethod.POST, request, String.class);
            Assert.notNull(response.getBody(), "Response body is null");

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            String botReply = jsonResponse.path("choices").get(0).path("message").path("content").asText();
            logger.info("KatBot replied: {}", botReply);
            event.getMessage().reply(botReply).queue();

        } catch (Exception e) {
            logger.error("Error processing GPT-3 request", e);
            event.getChannel().sendMessage("Sorry, I couldn't process your request at the moment.").queue();
        }
    }

    @Override
    public boolean isViable(MessageReceivedEvent event) {
        return event.getMessage().getContentDisplay().startsWith("k@t");
    }
}