package ovh.mythmc.social.bukkit.wrappers;

import java.util.Collection;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialRendererContext;
import ovh.mythmc.social.api.events.chat.SocialChatMessageReceiveEvent;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.wrappers.ChatEventWrapper;

public final class BukkitChatEventWrapper extends ChatEventWrapper<AsyncPlayerChatEvent> {

    @Override
    public Component message(AsyncPlayerChatEvent event) {
        return Component.text(event.getMessage());
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
    public void render(AsyncPlayerChatEvent event, SocialRendererContext context) {
        event.getRecipients().forEach(player -> {
            SocialUser recipient = Social.get().getUserManager().get(player.getUniqueId());

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
                return;

            Component component = Component.empty()
                .append(context.prefix())
                .append(socialChatMessageReceiveEvent.getMessage()
                    .applyFallbackStyle(Style.style(ClickEvent.suggestCommand("(re:#" + context.replyId() + ") "))));

            recipient.sendMessage(component);
        });

        // This allows the message to be logged in console and sent to plugins such as DiscordSRV
        event.getRecipients().clear();
        event.setFormat("(" + context.channel().getName() + ") %s: %s");
    }

    @Override
    @EventHandler
    public void on(AsyncPlayerChatEvent event) {
        super.on(event);
    }
    
}
