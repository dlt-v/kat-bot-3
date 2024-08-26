package com.katbot.eventListeners;

import com.katbot.commands.Poll.Poll;
import com.katbot.commands.Poll.PollManager;
import com.katbot.commands.Poll.Vote;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ButtonInteractionListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildMessageListener.class);
    private static final String testingChannelID = System.getenv("testing-channel-id");
    private static final String testingUserID = System.getenv("testing-user-id");

    private final PollManager pollManager = PollManager.getInstance();

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event)
    {

        LOGGER.info("Button \"{}\" (Button id:{}, Message id: {}) was reacted with.", event.getButton().getLabel(), event.getButton().getId(), event.getMessage().getId());

        Poll poll = pollManager.getPoll(event.getMessage().getIdLong());

        if (poll == null) {
            LOGGER.error("Poll with id: {} not found. Probably not cached in memory.", event.getMessage().getIdLong());
            event.reply("Poll not found. Probably not cached in memory.").setEphemeral(true).queue();
            return;
        }

        if (poll.containsVote(event.getUser().getIdLong())) {
            LOGGER.info("User with id: {} already voted in this poll.", event.getUser().getIdLong());
            event.reply("You already voted on this poll.").setEphemeral(true).queue();
            return;
        }

        event.reply("You chose: \"" + event.getButton().getLabel() + "\"").setEphemeral(true).queue();

        if (pollManager.addVote(poll.getId(), new Vote(event.getUser().getIdLong()))) {
            LOGGER.info("Vote added.");
        } else {
            LOGGER.error("Vote not added.");
        }

        MessageEmbed oldEmbed = event.getMessage().getEmbeds().get(0);

        MessageEmbed newEmbed = buildNewEmbed(event, oldEmbed);
        List<MessageEmbed> embeds = new ArrayList<>();
        embeds.add(newEmbed);
        event.getMessage().editMessageEmbeds(embeds).queue();
    }

    @NotNull
    private static MessageEmbed buildNewEmbed(@NotNull ButtonInteractionEvent event, MessageEmbed oldEmbed) {
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
        return newEmbedBuilder.build();
    }
}

