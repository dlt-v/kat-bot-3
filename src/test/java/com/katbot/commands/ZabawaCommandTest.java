package com.katbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ZabawaCommandTest {
    @Mock
    private ZabawaCommand zabawaCommand;
    @Mock
    private MessageReceivedEvent mockEvent;
    @Mock
    private MessageChannelUnion mockChannel;
    @Mock
    private User mockUser;
    @Mock
    private Message mockMessage;
    @Mock
    private MessageCreateAction mockMessageAction;

    @BeforeEach
    public void setUp() {
        // Initialize mocks created with the @Mock annotation
        MockitoAnnotations.openMocks(this);

        // Instantiate the object to test
        zabawaCommand = new ZabawaCommand();

        // Configure the mock objects to return expected values
        when(mockEvent.getChannel()).thenReturn(mockChannel);
        when(mockEvent.getAuthor()).thenReturn(mockUser);
        when(mockEvent.getMessage()).thenReturn(mockMessage);
        when(mockUser.getId()).thenReturn("1234");
        when(mockChannel.sendMessage(anyString())).thenReturn(mockMessageAction);
    }

    @Test
    public void testZabawa() {
        when(mockMessage.getContentDisplay()).thenReturn("kat zabawa");

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        zabawaCommand.execute(mockEvent, new String[]{});

        verify(mockChannel).sendMessage(messageCaptor.capture());

        String responseMessage = messageCaptor.getValue();
        assertTrue(responseMessage.matches("https://cdn\\.discordapp\\.com/attachments/\\d+/\\d+/[\\w-]+\\.mp4|mov\n"), "Response should match the pattern.");
    }
}
