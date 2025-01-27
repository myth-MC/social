package ovh.mythmc.social.paper.wrappers;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialRendererContext;
import ovh.mythmc.social.api.events.chat.SocialChatMessageReceiveEvent;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.wrappers.ChatEventWrapper;

public final class PaperChatEventWrapper extends ChatEventWrapper<AsyncChatEvent> {

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
    public void render(AsyncChatEvent event, SocialRendererContext context) {
        // Set renderer
        event.renderer((source, sourceDisplayName, message, viewer) -> {
            Optional<UUID> recipientUuid = viewer.get(Identity.UUID);
            if (recipientUuid.isEmpty()) // Fallback message (console)
                return Component.empty()
                    .append(context.prefix())
                    .append(context.message());

            SocialUser recipient = Social.get().getUserManager().get(recipientUuid.get());
            
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
    
            return Component.empty()
                .append(context.prefix())
                .append(socialChatMessageReceiveEvent.getMessage()
                    .applyFallbackStyle(Style.style(ClickEvent.suggestCommand("(re:#" + context.replyId() + ") "))));
        });
    }

    @EventHandler
    public void on(AsyncChatEvent event) {
        super.on(event);
    }

}
