package ovh.mythmc.social.paper.adapter;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.bukkit.adapter.ChatEventAdapter;
import ovh.mythmc.social.api.callback.message.SocialMessageReceive;
import ovh.mythmc.social.api.callback.message.SocialMessageReceiveCallback;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.user.AbstractSocialUser;

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

            return !viewerUuids.contains(viewerUuid);
        });
    }

    @Override
    public void cancel(AsyncChatEvent event) {
        // I know this sucks, but for some reason AsyncChatEvent is not cancellable?
        event.viewers().clear();
        event.setCancelled(true);
    }

    @Override
    @EventHandler
    public void on(AsyncChatEvent event) {
        final boolean isCancelled = event.viewers().stream()
            .noneMatch(audience -> audience instanceof Player);

        if (!isCancelled)
            super.on(event);
    }

    @EventHandler
    public void cancellationWorkaround(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            event.getRecipients().clear();
    }

    @Override
    public void render(AsyncChatEvent event, @NotNull SocialRegisteredMessageContext messageContext) {
        event.renderer((source, sourceDisplayName, component, viewer) -> {
            final var renderer = Social.get().getChatManager().getRegisteredRenderer(viewer);
            if (renderer == null)
                return Component.empty();

            final var context = renderer.render(viewer, messageContext);
            if (context == null)
                return Component.empty();

            // Cancel if message is empty
            if (context.message().equals(Component.empty()))
                return Component.empty();

            // Define variables
            final var prefix = context.prefix();
            var message = context.message();

            // Trigger message receive event if recipient is a SocialUser
            var mapResult = renderer.mapFromAudience(viewer);
            if (mapResult.isSuccess() && mapResult.result() instanceof AbstractSocialUser recipient) {
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
                    return null;

                message = callback.message();
            }

            return Component.empty()
                .append(prefix)
                .append(message);
        });
    }
    
}
