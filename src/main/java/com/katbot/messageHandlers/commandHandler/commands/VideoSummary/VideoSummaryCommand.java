package com.katbot.messageHandlers.commandHandler.commands.VideoSummary;

import com.katbot.messageHandlers.ChatGptHandler;
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
    private final ChatGptHandler chatGptHandler;

    public VideoSummaryCommand(ChatGptHandler chatGptHandler) {
        this.chatGptHandler = chatGptHandler;
    }

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

            String cleanText = fetchTranscript(videoUrl, fileBaseName);

            String summary = prepareCall(cleanText);

            // TODO: Make it another thread? So it doesn't block the bot
            // Make sure the files are deleted, in batch?
            // Switch to Gemini because it's cheaper, way cheaper.
            summary = summary.length() > 2000 ? summary.substring(0, 1995) + "..." : summary;
            event.getMessage().reply(summary).queue();
        } catch (Exception e) {
            logger.error("An error occurred in the summarising video process: ", e);
            String replyErrorMessage = e.getMessage().length() > 200 ? e.getMessage().substring(0, 200) + "..." : e.getMessage();
            event.getChannel().sendMessage("An error occurred while fetching subtitles: " + replyErrorMessage).queue();
        }

    }

    @Override
    public List<String> getAliases() {
        return List.of("sumup");
    }

    private String fetchTranscript(String videoUrl, String fileBaseName) throws Exception {
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
            logger.error("yt-dlp process failed with exit code: {}", process.exitValue());
            throw new RuntimeException("yt-dlp process failed. Could not find subtitles for this video.");
        }

        // 2. Locate and read the file
        // yt-dlp appends the lang and extension, e.g., "subs_123.en.srt"
        File tempFile = new File(fileBaseName + ".en.srt");
        if (!tempFile.exists()) {
            logger.error("Subtitle file not found: {}", tempFile.getAbsolutePath());
            throw new RuntimeException("Subtitle file not found");
        }

        String rawContent = Files.readString(tempFile.toPath());

        // 3. Clean up (Delete file)
        tempFile.delete();

        // 4. Handle output
        return cleanSrt(rawContent);
    }

    private String cleanSrt(String srtContent) {
        return srtContent
                .replaceAll("\\d+\\n\\d{2}:\\d{2}:\\d{2},\\d{3} --> \\d{2}:\\d{2}:\\d{2},\\d{3}", "") // Remove timestamps
                .replaceAll("<[^>]*>", "") // Remove any HTML tags
                .replaceAll("(?m)^[ \\t]*\\r?\\n", "") // Remove empty lines
                .trim();
    }

    private String prepareCall(String videoTranscript) {
        String instructions = "Summarize the following video transcript into a concise summary, highlighting the main points and key takeaways. " +
                "Make sure it's formatted with markdown but for a discord message. Do not exceed 1500 characters.";

        return chatGptHandler.makeApiRequest(videoTranscript, instructions, "gpt-4.1-2025-04-14");
    }
}
