package com.katbot.commands.Timestamp;

import com.katbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TimestampCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(TimestampCommand.class.getName());

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        logger.debug("Timestamp command executed with args: {}", (Object) args);

        // basic use:
        //
        //kat ts 1 hour
        //kat ts 2 hours
        //kat ts 1 week
        // Get amount of time to add
        int amount = Integer.parseInt(args[0]);

        // Get current time
        long epochTime = System.currentTimeMillis() / 1000;

        // Get type of unit (minute, hour, day, week)
        String unit = args[1];

        switch (unit) {
            case "minute":
            case "minutes":
            case "min":
                epochTime += amount * 60L;
                break;
            case "hour":
            case "hours":
                epochTime += amount * 3600L;
                break;
            case "day":
            case "days":
                epochTime += amount * 86400L;
                break;
            case "week":
            case "weeks":
                epochTime += amount * 604800L;
                break;
            default:
                event.getChannel().sendMessage("Invalid unit of time. Use minute, hour, day, or week.").queue();
                return;
        }

        // if args contain -r flag, then round the timestamp to the nearest hour
        if (args.length > 2 && (args[2].equals("-r") || args[2].equals("-round"))) {
            epochTime = Math.round(epochTime / 3600.0) * 3600;
        }

        // Reply with the formatted discord timestamp.
        EmbedBuilder embedBuilder = formatAnswer(epochTime);
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private EmbedBuilder formatAnswer(long epochTime) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Timestamp");
        String description = "<t:" + epochTime + ":R> `<t:" + epochTime + ":R>`\n\n" +
                "<t:" + epochTime + ":t> `<t:" + epochTime + ":t>`\n\n" +
                "<t:" + epochTime + ":D> `<t:" + epochTime + ":D>`\n\n" +
                "<t:" + epochTime + ":f> `<t:" + epochTime + ":f>`\n\n" +
                "<t:" + epochTime + ":F> `<t:" + epochTime + ":F>`";
        embedBuilder.setDescription(description);
        return embedBuilder;
    }

    @Override
    public List<String> getAliases() {
        return List.of("ts", "timestamp");
    }
}
