package ovh.mythmc.social.api.bukkit.adapter;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.api.callback.message.SocialMessagePrepare;
import ovh.mythmc.social.api.callback.message.SocialMessagePrepareCallback;
import ovh.mythmc.social.api.callback.message.SocialMessageSend;
import ovh.mythmc.social.api.callback.message.SocialMessageSendCallback;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.context.SocialParserContext;

public abstract class ChatEventAdapter<E extends PlayerEvent & Cancellable> implements Listener {

    protected abstract Component message(E event);

    protected abstract SignedMessage signedMessage(E event);

    protected abstract void viewers(E event, Set<Audience> viewers);

    protected abstract void render(E event, @NotNull SocialRegisteredMessageContext messageContext);

    protected abstract void cancel(E event);
    
    public void on(E event) {
        if (event.isCancelled())
            return;

        // Set variables
        final var sender = BukkitSocialUser.from(event.getPlayer());
        if (sender == null) {
            cancel(event);
            return;
        }

        var channel = sender.mainChannel();
        var plainMessage = PlainTextComponentSerializer.plainText().serialize(message(event));

        // Flood filter
        if (Social.get().getConfig().getChat().getFilter().isEnabled() && Social.get().getConfig().getChat().getFilter().isFloodFilter()) {
            int floodFilterCooldownInMilliseconds = Social.get().getConfig().getChat().getFilter().getFloodFilterCooldownInMilliseconds();

            if (System.currentTimeMillis() - sender.latestMessageInMilliseconds().get() < floodFilterCooldownInMilliseconds &&
                    sender.player().isPresent() && !sender.checkPermission("social.filter.bypass")) {

                Social.get().getTextProcessor().parseAndSend(sender, channel, Social.get().getConfig().getMessages().getErrors().getTypingTooFast(), Social.get().getConfig().getMessages().getChannelType());
                cancel(event);
                return;
            }
        }

        // Cancel and show error message if sender is muted
        if (Social.get().getUserManager().isMuted(sender, channel)) {
            Social.get().getTextProcessor().parseAndSend(sender, channel, Social.get().getConfig().getMessages().getErrors().getCannotSendMessageWhileMuted(), Social.get().getConfig().getMessages().getChannelType());
            cancel(event);
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
        if (plainMessage.isBlank()) {
            cancel(event);
            return;
        }

        // Set viewers
        final Set<Audience> viewers = new HashSet<>(channel.members());

        // Add socialspy
        viewers.addAll(Social.get().getUserService().getSocialSpyUsers());

        // Add console
        viewers.add(SocialAdventureProvider.get().console());

        // Prepare message event
        final var preCallback = new SocialMessagePrepare(sender, channel, viewers, plainMessage, replyId);
        SocialMessagePrepareCallback.INSTANCE.invoke(preCallback);

        // Set viewers
        viewers(event, preCallback.viewers());

        if (preCallback.cancelled()) {
            cancel(event);
            return;
        }

        // Update variables
        channel = preCallback.channel();
        plainMessage = preCallback.plainMessage();

        // Get message context
        final var message = new SocialMessageContext(sender, channel, Set.copyOf(channel.members()), plainMessage, replyId, signedMessage(event));

        // Filter message
        final var filteredMessage = Social.get().getTextProcessor().parsePlayerInput(
            SocialParserContext.builder(sender, Component.text(plainMessage))
                .channel(channel)
                .build());

        // Register message in history
        final var registeredMessage = Social.get().getChatManager().getHistory().register(message, filteredMessage);

        // Get ID to reply to this message
        Integer idToReply = registeredMessage.id();
        if (replyId != null)
            idToReply = Integer.min(replyId, idToReply);

        final var postCallback = new SocialMessageSend(sender, channel, filteredMessage, registeredMessage.id(), idToReply, event.isCancelled());
        SocialMessageSendCallback.INSTANCE.invoke(postCallback);

        // Let the impl handle the rest
        render(
            event, 
            new SocialRegisteredMessageContext(
                registeredMessage.id(), 
                registeredMessage.timestamp(), 
                registeredMessage.sender(), 
                registeredMessage.channel(), 
                registeredMessage.viewers(),
                registeredMessage.message(),
                registeredMessage.rawMessage(), 
                registeredMessage.replyId(),
                registeredMessage.signedMessage().orElse(null))
        );

        // Update sender's latest message
        sender.latestMessageInMilliseconds().set(System.currentTimeMillis());
    }

    private static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
}
