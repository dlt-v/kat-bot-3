package com.katbot.messageHandlers.commandHandler.commands.VideoSummary;

import com.katbot.messageHandlers.commandHandler.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Component
public class VideoSummaryCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(VideoSummaryCommand.class);

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (args.length == 0) {
            logger.error("No video link provided for summarization.");
            event.getChannel().sendMessage("Please provide a video link to summarize!").queue();
            return;
        }

        event.getChannel().sendMessage("Summarizing video...").queue();
        String videoUrl = args[0];
        String fileBaseName = "subs_" + System.currentTimeMillis();

        try {

            // 1. Run yt-dlp to fetch subtitles.
            // For the god's sake, I can't make yt-dlp to just output to stdout.
            // Apparently it's a bug?  https://github.com/yt-dlp/yt-dlp/issues/9165
            ProcessBuilder pb = new ProcessBuilder(
                    "yt-dlp",
                    "--skip-download",
                    "--write-auto-subs",
                    "--sub-langs", "en.*",
                    "--sub-format", "srt",
                    "-o", fileBaseName,
                    videoUrl
            );

            Process process = pb.start();
            if (process.waitFor() != 0) {
                event.getChannel().sendMessage("Could not find subtitles for this video.").queue();
                return;
            }

            // 2. Locate and read the file
            // yt-dlp appends the lang and extension, e.g., "subs_123.en.srt"
            File tempFile = new File(fileBaseName + ".en.srt");
            if (!tempFile.exists()) {
                // Fallback check for different lang suffixes if en.srt isn't exact
                event.getChannel().sendMessage("Subtitle file was not generated.").queue();
                return;
            }

            String rawContent = Files.readString(tempFile.toPath());

            // 3. Clean up (Delete file)
            tempFile.delete();

            // 4. Handle output
            String cleanText = cleanSrt(rawContent);
            // TODO: Use chatgpt to summarize this stuff later
            // TODO: Make it another thread? So it doesn't block the bot
            event.getMessage().reply("Transcript preview:\n" + cleanText.substring(0, Math.min(cleanText.length(), 500))).queue();
        } catch (Exception e) {
            logger.error("Error fetching subtitles: ", e);
            event.getChannel().sendMessage("An error occurred while fetching subtitles.").queue();
            return;
        }

    }

    @Override
    public List<String> getAliases() {
        return List.of("sumup");
    }

    private String cleanSrt(String srtContent) {
        return srtContent
                .replaceAll("\\d+\\n\\d{2}:\\d{2}:\\d{2},\\d{3} --> \\d{2}:\\d{2}:\\d{2},\\d{3}", "") // Remove timestamps
                .replaceAll("<[^>]*>", "") // Remove any HTML tags
                .replaceAll("(?m)^[ \\t]*\\r?\\n", "") // Remove empty lines
                .trim();
    }
}
