package ovh.mythmc.social.bukkit.adapter;

import java.util.Collection;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.bukkit.adapter.ChatEventAdapter;
import ovh.mythmc.social.api.callback.message.SocialMessageReceive;
import ovh.mythmc.social.api.callback.message.SocialMessageReceiveCallback;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;

public final class BukkitChatEventAdapter extends ChatEventAdapter<AsyncPlayerChatEvent> {

    @Override
    public Component message(AsyncPlayerChatEvent event) {
        return Component.text(event.getMessage());
    }

    @Override
    public SignedMessage signedMessage(AsyncPlayerChatEvent event) {
        return null;
    }

    @Override
    public void viewers(AsyncPlayerChatEvent event, Set<Audience> viewers) {
        final Collection<Player> players = viewers.stream()
            .filter(audience -> audience.get(Identity.UUID).isPresent())
            .map(audience -> Bukkit.getPlayer(audience.get(Identity.UUID).get()))
            .toList();
        
        event.getRecipients().clear();
        event.getRecipients().addAll(players);
    }

    @Override
    @EventHandler
    public void on(AsyncPlayerChatEvent event) {
        super.on(event);
    }

	@Override
	public void render(AsyncPlayerChatEvent event, @NotNull SocialRegisteredMessageContext messageContext) {
        event.getRecipients().forEach(player -> {
            final var recipient = Social.get().getUserService().getByUuid(player.getUniqueId()).get();
            
            var renderer = Social.get().getChatManager().getRegisteredRenderer(recipient);
            if (renderer == null)
                return;

            var context = renderer.render(recipient, messageContext);

            // A null context means that the event should be cancelled
            if (context == null) {
                return;
            }

            // Cancel event if message is empty
            if (context.message() == Component.empty()) {
                return;
            }

            // Define variables
            var prefix = context.prefix();
            var message = context.message();

            // Trigger message receive event
            var callback = new SocialMessageReceive(
                context.sender(), 
                recipient, 
                context.channel(), 
                context.message(), 
                context.messageId(), 
                context.replyId());
    
            SocialMessageReceiveCallback.INSTANCE.invoke(callback);
            if (callback.cancelled())
                return;

            message = callback.message();

            var component = prefix.append(message);

            recipient.sendMessage(component);
        });

        // This allows the message to be logged in console and sent to plugins such as DiscordSRV
        event.getRecipients().clear();
        event.setFormat("(" + messageContext.channel().name() + ") %s: %s");
	}
    
}
