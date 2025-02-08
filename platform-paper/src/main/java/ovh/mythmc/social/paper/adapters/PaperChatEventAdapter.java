package ovh.mythmc.social.paper.adapters;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.events.chat.SocialChatMessageReceiveEvent;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.adapters.ChatEventAdapter;

public final class PaperChatEventAdapter extends ChatEventAdapter<AsyncChatEvent> {

    @Override
    public Component message(AsyncChatEvent event) {
        return event.message();
    }

    @Override
    public SignedMessage signedMessage(AsyncChatEvent event) {
        return event.signedMessage();
    }

    @Override
    public void viewers(AsyncChatEvent event, Set<Audience> viewers) {
        event.viewers().removeIf(viewer -> {
            if (viewer.get(Identity.UUID).isEmpty())
                return false;

            UUID viewerUuid = viewer.get(Identity.UUID).get();
            Collection<UUID> viewerUuids = viewers.stream()
                .filter(audience -> audience.get(Identity.UUID).isPresent())
                .map(audience -> audience.get(Identity.UUID).get())
                .toList();

            if (viewerUuids.contains(viewerUuid))
                return false;

            return true;
        });
    }

    @Override
    @EventHandler
    public void on(AsyncChatEvent event) {
        super.on(event);
    }

    @Override
    public void render(AsyncChatEvent event, @NotNull SocialRegisteredMessageContext messageContext) {
        event.renderer((source, sourceDisplayName, component, viewer) -> {
            var renderer = Social.get().getChatManager().getRenderer(viewer);
            if (renderer == null)
                return Component.empty();

            var context = renderer.render(viewer, messageContext);
            if (context == null)
                return Component.empty();

            // Cancel if message is empty
            if (context.message().equals(Component.empty()))
                return Component.empty();

            // Define variables
            var prefix = context.prefix();
            var message = context.message();

            // Trigger message receive event if recipient is a SocialUser
            var mapResult = renderer.mapFromAudience(viewer);
            if (mapResult.isSuccess() && mapResult.result() instanceof SocialUser recipient) {
                SocialChatMessageReceiveEvent socialChatMessageReceiveEvent = new SocialChatMessageReceiveEvent(
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
                    return null;

                message = socialChatMessageReceiveEvent.getMessage();
            }

            return Component.empty()
                .append(prefix)
                .append(message);
        });
    }
    
}
