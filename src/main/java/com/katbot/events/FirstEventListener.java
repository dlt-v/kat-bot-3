package com.katbot.events;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FirstEventListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(FirstEventListener.class);
    private static final String testingChannelID = System.getenv("testing-channel-id");
    private static final String testingUserID = System.getenv("testing-user-id");
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        // The user who sent the message
        User author = event.getAuthor();
        // This is a special class called a "union", which allows you to perform specialization to more concrete types such as TextChannel or NewsChannel
        MessageChannelUnion channel = event.getChannel();
        // The actual message sent by the user, this can also be a message the bot sent itself, since you *do* receive your own messages after all
        Message message = event.getMessage();

        if (author.isBot()) return;

        // Check whether the message was sent in a guild / server
        if (event.isFromGuild())
        {
            // This is a message from a server
            logger.info(String.format("[%s] [%#s] %#s: %s",
                    event.getGuild().getName(), // The name of the server the user sent the message in, this is generally referred to as "guild" in the API
                    channel, // The %#s makes use of the channel name and displays as something like #general
                    author,  // The %#s makes use of User#getAsTag which results in something like minn or Minn#1337
                    message.getContentDisplay() // This removes any unwanted mention syntax and converts it to a readable string
            ));
        }
        else
        {
            // This is a message from a private channel
            logger.debug(String.format("[direct] %#s: %s\n",
                    author, // same as above
                    message.getContentDisplay()
            ));
        }

        // Using specialization, you can check concrete types of the channel union

        if (channel.getType() == ChannelType.TEXT  && event.getChannel().getId().equals(testingChannelID) && author.getId().equals(testingUserID))
        {
            event.getChannel().sendMessage(
                    String.format("Hello! You wrote : \"%s\"", message.getContentDisplay())
            ).queue();
        }

//        if (channel.getType().isThread())
//        {
//        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event)
    {

        System.out.println("A user reacted to a message!");
    }
}
