package com.katbot.messageHandlers;

import com.katbot.repositories.broadcastChannel.BroadcastChannel;
import com.katbot.repositories.broadcastChannel.BroadcastChannelRepository;
import com.katbot.repositories.user.UserRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BroadcastChannelHandler implements  Handler {

    private final BroadcastChannelRepository broadcastChannelRepository;
    private final UserRepository userRepository;

    public BroadcastChannelHandler(
            BroadcastChannelRepository broadcastChannelRepository,
            UserRepository userRepository
    ) {
        this.broadcastChannelRepository = broadcastChannelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        // Check if user is subscribed to the broadcast feature
        if (!checkIfUserHasBroadcastEnabled(event.getAuthor().getIdLong())) {
            return;
        }

        Long originalChannelId = event.getChannel().getIdLong();
        // If a broadcast channel then send a message to all broadcast channels except the original one.
        List<BroadcastChannel> broadcastChannels = getBroadcastChannels();

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(0x761b82);
        embedBuilder.setAuthor(event.getAuthor().getName() + " wrote:", null, event.getAuthor().getAvatarUrl());
        embedBuilder.setTitle("> " + event.getMessage().getContentDisplay());

        String serverName = broadcastChannels.stream()
                .filter(channel -> channel.id().equals(originalChannelId))
                .findFirst()
                .map(BroadcastChannel::serverName)
                .orElse("Unknown");

        embedBuilder.setFooter("from " + serverName, event.getGuild().getIconUrl());

        for (BroadcastChannel broadcastChannel : broadcastChannels) {
            if (!broadcastChannel.id().equals(originalChannelId)) {
                // Find the TextChannel by ID and send the message
                TextChannel channel = event.getJDA().getTextChannelById(broadcastChannel.id());
                if (channel != null) {
                    channel.sendMessageEmbeds(embedBuilder.build()).queue();
                }
            }
        }

    }

    private boolean checkIfUserHasBroadcastEnabled(Long userId) {
        return userRepository.doesUserHaveBroadcastEnabled(userId);
    }

    public boolean isBroadcastChannel(MessageReceivedEvent event) {
        Long channelId = event.getChannel().getIdLong();
        List<BroadcastChannel> broadcastChannels = getBroadcastChannels();

        return broadcastChannels.stream()
                .anyMatch(channel -> channel.id().equals(channelId));
    }

    private List<BroadcastChannel> getBroadcastChannels() {
        return broadcastChannelRepository.getBroadcastChannelIDs();
    }
}
