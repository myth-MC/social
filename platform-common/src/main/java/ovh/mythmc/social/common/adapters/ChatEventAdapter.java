package ovh.mythmc.social.common.adapters;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.events.chat.SocialChatMessagePrepareEvent;
import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.context.SocialParserContext;

public abstract class ChatEventAdapter<E extends PlayerEvent & Cancellable> implements Listener {

    private static ChatEventAdapter<? extends PlayerEvent> instance;

    public static ChatEventAdapter<? extends PlayerEvent> get() { return instance; }

    public static void set(@NotNull ChatEventAdapter<? extends PlayerEvent> c) { instance = c; }

    public abstract Component message(E event);

    public abstract SignedMessage signedMessage(E event);

    public abstract void viewers(E event, Set<Audience> viewers);

    public abstract void render(E event, @NotNull SocialRegisteredMessageContext messageContext);

    public void on(E event) {
        // Set variables
        var sender = Social.get().getUserManager().getByUuid(event.getPlayer().getUniqueId());
        if (sender == null)
            return;

        var channel = sender.getMainChannel();
        var plainMessage = PlainTextComponentSerializer.plainText().serialize(message(event));

        // Flood filter
        if (Social.get().getConfig().getSettings().getChat().getFilter().isFloodFilter()) {
            int floodFilterCooldownInMilliseconds = Social.get().getConfig().getSettings().getChat().getFilter().getFloodFilterCooldownInMilliseconds();

            if (System.currentTimeMillis() - sender.getLatestMessageInMilliseconds() < floodFilterCooldownInMilliseconds &&
                    !sender.getPlayer().hasPermission("social.filter.bypass")) {

                Social.get().getTextProcessor().parseAndSend(sender, channel, Social.get().getConfig().getMessages().getErrors().getTypingTooFast(), Social.get().getConfig().getMessages().getChannelType());
                event.setCancelled(true);
                return;
            }
        }

        // Cancel and show error message if sender is muted
        if (Social.get().getUserManager().isMuted(sender, channel)) {
            Social.get().getTextProcessor().parseAndSend(sender, channel, Social.get().getConfig().getMessages().getErrors().getCannotSendMessageWhileMuted(), Social.get().getConfig().getMessages().getChannelType());
            event.setCancelled(true);
            return;
        }

        // Check if message is a reply
        Integer replyId = null;
        if (plainMessage.startsWith("(re:#") && plainMessage.contains(")")) {
            String replyIdString = plainMessage.substring(5, plainMessage.indexOf(")"));
            replyId = tryParse(replyIdString);
            plainMessage = plainMessage.replace("(re:#" + replyId + ")", "").trim();
        }

        // Cancel if message is empty
        if (plainMessage.isBlank())
            return;

        // Prepare message event
        SocialChatMessagePrepareEvent chatMessagePrepareEvent = new SocialChatMessagePrepareEvent(sender, channel, plainMessage, replyId);
        Bukkit.getPluginManager().callEvent(chatMessagePrepareEvent);
        if (chatMessagePrepareEvent.isCancelled())
            return;

        // Update variables
        channel = chatMessagePrepareEvent.getChannel();
        plainMessage = chatMessagePrepareEvent.getRawMessage();

        // Set viewers
        Set<Audience> viewers = new HashSet<>(channel.getMembers());

        // Add socialspy
        viewers.addAll(Social.get().getUserManager().getSocialSpyUsers());

        // Add console
        viewers.add(SocialAdventureProvider.get().console());

        // Set viewers
        viewers(event, viewers);

        // Get message context
        var message = new SocialMessageContext(sender, channel, Set.copyOf(channel.getMembers()), plainMessage, replyId, signedMessage(event));

        // Filter message
        var filteredMessage = Social.get().getTextProcessor().parsePlayerInput(SocialParserContext.builder()
            .user(sender)
            .message(Component.text(plainMessage))
            .build());

        // Register message in history
        var registeredMessage = Social.get().getChatManager().getHistory().register(message, filteredMessage);

        // Get ID to reply to this message
        Integer idToReply = registeredMessage.id();
        if (replyId != null)
            idToReply = Integer.min(replyId, idToReply);

        // Let the impl handle the rest
        render(
            event, 
            new SocialRegisteredMessageContext(
                registeredMessage.id(), 
                registeredMessage.timestamp(), 
                registeredMessage.sender(), 
                registeredMessage.chatChannel(), 
                registeredMessage.viewers(), 
                registeredMessage.message().applyFallbackStyle(Style.style(ClickEvent.suggestCommand("(re:#" + idToReply + ") "))), 
                registeredMessage.rawMessage(), 
                registeredMessage.replyId(),
                registeredMessage.signedMessage())
        );

        // Update sender's latest message
        Social.get().getUserManager().setLatestMessage(sender, System.currentTimeMillis());
    }

    private static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
}
