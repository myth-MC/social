package ovh.mythmc.social.bukkit.adapters;

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
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.events.chat.SocialChatMessageReceiveEvent;
import ovh.mythmc.social.common.adapters.ChatEventAdapter;

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
        Collection<Player> players = viewers.stream()
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
            var recipient = Social.get().getUserManager().getByUuid(player.getUniqueId());
            
            var renderer = Social.get().getChatManager().getRenderer(recipient);
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
            var socialChatMessageReceiveEvent = new SocialChatMessageReceiveEvent(
                context.sender(), 
                recipient,
                context.channel(),
                context.message(), 
                context.plainMessage(),
                context.replyId(),
                context.messageId()
            );
    
            Bukkit.getPluginManager().callEvent(socialChatMessageReceiveEvent);
            if (socialChatMessageReceiveEvent.isCancelled())
                return;

            message = socialChatMessageReceiveEvent.getMessage();

            var component = prefix.append(message);

            recipient.sendMessage(component);
        });

        // This allows the message to be logged in console and sent to plugins such as DiscordSRV
        event.getRecipients().clear();
        event.setFormat("(" + messageContext.chatChannel().getName() + ") %s: %s");
	}
    
}
