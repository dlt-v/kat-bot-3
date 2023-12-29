package com.katbot.commands;

import com.katbot.commands.Roll.RollCommand;
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

public class RollCommandTest {
    @Mock
    private RollCommand rollCommand;
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
        rollCommand = new RollCommand();

        // Configure the mock objects to return expected values
        when(mockEvent.getChannel()).thenReturn(mockChannel);
        when(mockEvent.getAuthor()).thenReturn(mockUser);
        when(mockEvent.getMessage()).thenReturn(mockMessage);
        when(mockUser.getId()).thenReturn("1234");
        when(mockChannel.sendMessage(anyString())).thenReturn(mockMessageAction);
    }

    @Test
    public void testRollNoArguments() {
        // Simulate command without arguments
        when(mockMessage.getContentDisplay()).thenReturn("kat roll");

        // Capture the message sent to the channel
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        // Execute the command
        rollCommand.execute(mockEvent, new String[]{});

        // Verify sendMessage was called and capture the message
        verify(mockChannel).sendMessage(messageCaptor.capture());

        // Assert the message matches the expected pattern
        String responseMessage = messageCaptor.getValue();
        assertTrue(responseMessage.matches("<@1234> rolled \\d."), "Response should match the pattern but it returns: " + responseMessage);
    }

    @Test
    public void testRollSingleDie() {
        when(mockMessage.getContentDisplay()).thenReturn("kat roll d12");

        rollCommand.execute(mockEvent, new String[]{"d12"});

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockChannel).sendMessage(messageCaptor.capture());

        String responseMessage = messageCaptor.getValue();
        assertTrue(responseMessage.matches("<@1234> rolled \\d{1,2}."),
                "Response should match the pattern and be a number between 1 and 12.");
    }

    @Test
    public void testRollSingleDieWithModifier() {

        when(mockMessage.getContentDisplay()).thenReturn("/kat roll d2 +4");


        rollCommand.execute(mockEvent, new String[]{"d2", "+4"});


        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockChannel).sendMessage(messageCaptor.capture());

        String responseMessage = messageCaptor.getValue();
        assertTrue(responseMessage.matches("<@1234> rolled \\d \\+ 4 = \\d{1,2}."),
                "Response should match the pattern and be the result of the die roll plus the modifier.");
    }

    @Test
    public void testRollMultipleDiceWithModifier() {
        when(mockMessage.getContentDisplay()).thenReturn("/kat roll 2d2 +4");

        rollCommand.execute(mockEvent, new String[]{"2d2", "+4"});

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockChannel).sendMessage(messageCaptor.capture());

        String responseMessage = messageCaptor.getValue();
        assertTrue(responseMessage.matches("<@1234> rolled \\d \\+ \\d \\+ 4 = \\d{1,2}."),
                "Response should match the pattern and be a sum of the two dice rolls plus the modifier.");
    }

    @Test
    public void testRollMultipleDice() {
        when(mockMessage.getContentDisplay()).thenReturn("/kat roll 2d2");

        rollCommand.execute(mockEvent, new String[]{"2d2"});

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockChannel).sendMessage(messageCaptor.capture());

        String responseMessage = messageCaptor.getValue();
        assertTrue(responseMessage.matches("<@1234> rolled \\d \\+ \\d = \\d{1,2}."),
                "Response should match the pattern and be a sum of the two dice rolls.");
    }
}
