package com.katbot;

import com.katbot.events.ButtonInteractionListener;
import com.katbot.events.GuildMessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.EnumSet;

public class KatBot {
    public static void main(String[] args) {

        EnumSet<GatewayIntent> intents = EnumSet.of(
                // Enables MessageReceivedEvent for guild (also known as servers)
                GatewayIntent.GUILD_MESSAGES,
                // Enables the event for private channels (also known as direct messages)
                GatewayIntent.DIRECT_MESSAGES,
                // Enables access to message.getContentRaw()
                GatewayIntent.MESSAGE_CONTENT,
                // Enables MessageReactionAddEvent for guild
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                // Enables MessageReactionAddEvent for private channels
                GatewayIntent.DIRECT_MESSAGE_REACTIONS
        );


        String token = System.getenv("token");
        if (token == null) {
            throw new IllegalStateException("Discord bot token not found. Make sure the 'token' environment variable is set.");
        }
        JDABuilder builder = JDABuilder.createDefault(token, intents);
        builder.setActivity(Activity.watching("Netflix"));
        builder.addEventListeners(new GuildMessageListener());
        builder.addEventListeners(new ButtonInteractionListener());
        JDA jda = builder.build();
    }
}
