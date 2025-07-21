package com.katbot.eventListeners;

import com.katbot.commands.MinecraftStatus.MinecraftStatusCommand;
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
import java.util.Objects;

@Component
public class ButtonInteractionListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ButtonInteractionListener.class);

    private final PollManager pollManager = PollManager.getInstance();
    private final MinecraftStatusCommand minecraftStatusCommand = new MinecraftStatusCommand();

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event)
    {

        if (event.getInteraction().getButton().getId().equals("mc-status:check_again")) {
            minecraftStatusCommand.execute(event);
            return;
        }

        logger.info("Button \"{}\" (Button id:{}, Message id: {}) was reacted with.", event.getButton().getLabel(), event.getButton().getId(), event.getMessage().getId());

        Poll poll = pollManager.getPoll(event.getMessage().getIdLong());

        if (poll == null) {
            logger.error("Poll with id: {} not found. Probably not cached in memory.", event.getMessage().getIdLong());
            event.reply("Poll not found. Probably not cached in memory.").setEphemeral(true).queue();
            return;
        }

        if (poll.containsVote(event.getUser().getIdLong())) {
            logger.info("User with id: {} already voted in this poll.", event.getUser().getIdLong());
            event.reply("You already voted on this poll.").setEphemeral(true).queue();
            return;
        }

        event.reply("You chose: \"" + event.getButton().getLabel() + "\"").setEphemeral(true).queue();

        if (pollManager.addVote(poll.getId(), new Vote(event.getUser().getIdLong()))) {
            logger.info("Vote added.");
        } else {
            logger.error("Vote not added.");
        }

        MessageEmbed oldEmbed = event.getMessage().getEmbeds().get(0);

        MessageEmbed newEmbed = buildNewEmbed(event, oldEmbed);
        List<MessageEmbed> embeds = new ArrayList<>();
        embeds.add(newEmbed);
        event.getMessage().editMessageEmbeds(embeds).queue();
    }

    @NotNull
    private static MessageEmbed buildNewEmbed(@NotNull ButtonInteractionEvent event, @NotNull MessageEmbed oldEmbed) {

        EmbedBuilder newEmbedBuilder = new EmbedBuilder();
        newEmbedBuilder.setTitle(oldEmbed.getTitle());
        newEmbedBuilder.setDescription(oldEmbed.getDescription());
        newEmbedBuilder.setColor(oldEmbed.getColor());

        List<MessageEmbed.Field> fields = oldEmbed.getFields();
        for (MessageEmbed.Field field : fields) {
            int value = Integer.parseInt(Objects.requireNonNull(field.getValue()));
            if (Objects.equals(field.getName(), event.getButton().getLabel())) value++;
            newEmbedBuilder.addField(Objects.requireNonNull(field.getName()), String.valueOf(value), false);
        }
        return newEmbedBuilder.build();
    }
}

