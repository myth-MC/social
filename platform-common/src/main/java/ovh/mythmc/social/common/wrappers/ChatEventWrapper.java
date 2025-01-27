package ovh.mythmc.social.common.wrappers;

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
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.context.SocialRendererContext;
import ovh.mythmc.social.api.users.SocialUser;

public abstract class ChatEventWrapper<E extends PlayerEvent & Cancellable> implements Listener {

    private static ChatEventWrapper<? extends PlayerEvent> instance;

    public static ChatEventWrapper<? extends PlayerEvent> get() { return instance; }

    public static void set(@NotNull ChatEventWrapper<? extends PlayerEvent> c) { instance = c; }

    public abstract Component message(E event);

    public abstract SignedMessage signedMessage(E event);

    public abstract void viewers(E event, Set<Audience> viewers);

    public abstract void render(E event, SocialRendererContext context);

    public void on(E event) {
        // Set variables
        SocialUser sender = Social.get().getUserManager().get(event.getPlayer().getUniqueId());
        if (sender == null)
            return;

        ChatChannel channel = sender.getMainChannel();
        String plainMessage = PlainTextComponentSerializer.plainText().serialize(message(event));

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

        // Get rendered message
        SocialRendererContext context = Social.get().getChatManager().getRenderer().render(
            new SocialMessageContext(sender, channel, plainMessage, null, signedMessage(event))
        );

        // A null context means that the event should be cancelled
        if (context == null) {
            event.setCancelled(true);
            return;
        }

        // Cancel event if message is empty
        if (context.message() == Component.empty()) {
            event.setCancelled(true);
            return;
        }

        // Set viewers
        viewers(event, context.viewers());

        // Update sender's latest message
        Social.get().getUserManager().setLatestMessage(sender, System.currentTimeMillis());

        // Let the impl handle the rest
        render(event, context);
    }
    
}
