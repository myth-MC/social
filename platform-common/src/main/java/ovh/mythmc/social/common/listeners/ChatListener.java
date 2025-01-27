package ovh.mythmc.social.common.listeners;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialHistoryMessageContext;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.events.chat.*;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.wrappers.PlatformWrapper;

import java.util.UUID;

@RequiredArgsConstructor
public final class ChatListener implements Listener {

    private final JavaPlugin plugin;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        SocialUser user = Social.get().getUserManager().get(uuid);
        if (user == null) {
            // unexpected error;
            return;
        }

        Social.get().getChatManager().assignChannelsToPlayer(user);

        ChatChannel defaultChannel = Social.get().getChatManager().getDefaultChannel();
        if (defaultChannel == null) {
            Social.get().getLogger().error("Default channel is unavailable!");
            return;
        }

        Social.get().getUserManager().setMainChannel(user, defaultChannel);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (ChatChannel chatChannel : Social.get().getChatManager().getChannels()) {
            if (chatChannel.getMembers().contains(event.getPlayer().getUniqueId()))
                chatChannel.removeMember(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onSocialChatMessagePrepare(SocialChatMessagePrepareEvent event) {
        // Check if player still has permission to chat in their selected channel
        if (!Social.get().getChatManager().hasPermission(event.getSender(), event.getChannel())) {
            ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getSettings().getChat().getDefaultChannel());

            event.getChannel().removeMember(event.getSender());

            PlatformWrapper.get().runGlobalTask(plugin, () -> Social.get().getUserManager().setMainChannel(event.getSender(), defaultChannel));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSocialChatMessageReceive(SocialChatMessageReceiveEvent event) {
        // Play reply sound
        if (event.isReply())
            event.getSender().getPlayer().playSound(event.getSender().getPlayer(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.7F, 1.7F);

        if (event.getChannel().getPermission() == null)
            return;

        // We'll remove the player from this channel if they no longer have the required permission
        if (!Social.get().getChatManager().hasPermission(event.getRecipient(), event.getChannel())) {
            ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getSettings().getChat().getDefaultChannel());

            event.getChannel().removeMember(event.getRecipient());
            PlatformWrapper.get().runGlobalTask(plugin, () -> Social.get().getUserManager().setMainChannel(event.getRecipient(), defaultChannel));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void deleteButton(SocialChatMessageReceiveEvent event) {
        if (event.getRecipient().equals(event.getSender()) && 
            event.getRecipient().getPlayer().hasPermission("social.delete-messages.self")
            || event.getRecipient().getPlayer().hasPermission("social.delete-messages.others")) {
            
            SocialHistoryMessageContext message = Social.get().getChatManager().getHistory().getById(event.getMessageId());
            if (!Social.get().getChatManager().getHistory().canDelete(message))
                return;

            Component button = Social.get().getTextProcessor().parse(event.getRecipient(), event.getChannel(), Social.get().getConfig().getChat().getDeleteButtonIcon());
            Component hoverText = Social.get().getTextProcessor().parse(event.getRecipient(), event.getChannel(), Social.get().getConfig().getChat().getDeleteButtonHoverText());

            event.setMessage(event.getMessage()
                .appendSpace()
                .append(button
                    .hoverEvent(hoverText)
                    .clickEvent(ClickEvent.callback(ClickCallback.widen((audience) -> Social.get().getChatManager().getHistory().delete(message), Audience.class)))
                )
            );
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSocialChannelPreSwitch(SocialChannelPreSwitchEvent event) {
        if (!event.getChannel().getMembers().contains(event.getUser().getUuid()))
            event.getChannel().addMember(event.getUser().getUuid());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSocialChannelPostSwitch(SocialChannelPostSwitchEvent event) {
        SocialParserContext context = SocialParserContext.builder()
            .user(event.getUser())
            .channel(event.getChannel())
            .message(Component.text(Social.get().getConfig().getMessages().getCommands().getChannelChanged()))
            .build();

        Social.get().getTextProcessor().parseAndSend(context);
    }

}
