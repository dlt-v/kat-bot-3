package com.katbot.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ButtonInteractionListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(GuildMessageListener.class);
    private static final String testingChannelID = System.getenv("testing-channel-id");
    private static final String testingUserID = System.getenv("testing-user-id");

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event)
    {

        logger.info("Button \"" + event.getButton().getLabel() + "\" (id:" + event.getButton().getId() + ") was reacted with.");
        event.reply("You chose: \"" + event.getButton().getLabel() + "\"").setEphemeral(true).queue();


        MessageEmbed oldEmbed = event.getMessage().getEmbeds().get(0);

        EmbedBuilder newEmbedBuilder = new EmbedBuilder();
        newEmbedBuilder.setTitle(oldEmbed.getTitle());
        newEmbedBuilder.setDescription(oldEmbed.getDescription());
        newEmbedBuilder.setColor(oldEmbed.getColor());

        List<MessageEmbed.Field> fields = oldEmbed.getFields();
        for (MessageEmbed.Field field : fields) {
            int value = Integer.parseInt(field.getValue());
            if (field.getName().equals(event.getButton().getLabel())) value++;
            newEmbedBuilder.addField(field.getName(), String.valueOf(value), false);
        }
        List<MessageEmbed> embeds = new ArrayList<>();
        embeds.add(newEmbedBuilder.build());
        event.getMessage().editMessageEmbeds(embeds).queue();

    }
}

