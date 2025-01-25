package ovh.mythmc.social.common.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.database.model.IgnoredUser.IgnoreScope;
import ovh.mythmc.social.api.events.chat.SocialChatMessageReceiveEvent;
import ovh.mythmc.social.api.events.chat.SocialPrivateMessageEvent;

public final class IgnoreListener implements Listener {
     
    @EventHandler(priority = EventPriority.LOWEST)
    public void onSocialChatMessageReceive(SocialChatMessageReceiveEvent event) {
        if (!Social.get().getUserManager().isIgnored(event.getRecipient(), event.getSender()))
            return;

        IgnoreScope scope = Social.get().getUserManager().getIgnoreScope(event.getRecipient(), event.getSender());
        if (scope == IgnoreScope.ALL || scope == IgnoreScope.CHAT)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPrivateMessage(SocialPrivateMessageEvent event) {
        if (!Social.get().getUserManager().isIgnored(event.getRecipient(), event.getSender()))
            return;

        IgnoreScope scope = Social.get().getUserManager().getIgnoreScope(event.getRecipient(), event.getSender());
        if (scope == IgnoreScope.ALL || scope == IgnoreScope.PRIVATE_MESSAGES) {
            event.setCancelled(true);
            Social.get().getTextProcessor().parseAndSend(event.getSender(), event.getSender().getMainChannel(), Social.get().getConfig().getMessages().getErrors().getUserHasIgnoredYou(), Social.get().getConfig().getMessages().getChannelType());
        }
    }

}
