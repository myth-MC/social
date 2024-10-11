package ovh.mythmc.social.common.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.events.chat.SocialChannelPostSwitchEvent;
import ovh.mythmc.social.api.events.chat.SocialChannelPreSwitchEvent;
import ovh.mythmc.social.api.events.chat.SocialChatMessageReceiveEvent;
import ovh.mythmc.social.api.events.chat.SocialChatMessageSendEvent;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.util.PluginUtil;

import java.util.UUID;

public final class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);
        if (socialPlayer == null) {
            // unexpected error;
            return;
        }

        Social.get().getChatManager().assignChannelsToPlayer(socialPlayer);

        ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getSettings().getChat().getDefaultChannel());
        if (defaultChannel == null) {
            Social.get().getLogger().error("Default channel is unavailable!");
            return;
        }

        Social.get().getPlayerManager().setMainChannel(socialPlayer, defaultChannel);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (ChatChannel chatChannel : Social.get().getChatManager().getChannels()) {
            if (chatChannel.getMembers().contains(event.getPlayer().getUniqueId()))
                chatChannel.removeMember(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;

        UUID uuid = event.getPlayer().getUniqueId();
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);
        if (socialPlayer == null) {
            Social.get().getLogger().error("Unexpected error (missing SocialPlayer)");
            return;
        }

        if (socialPlayer.isMuted()) {
            event.setCancelled(true);
            return;
        }

        ChatChannel mainChannel = socialPlayer.getMainChannel();
        if (mainChannel == null) {
            Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getErrors().getUnexpectedError(), Social.get().getConfig().getMessages().getChannelType());
            event.setCancelled(true);
            return;
        }

        if (mainChannel.isPassthrough())
            return;

        // This will allow the message to be logged in console and sent to plugins such as DiscordSRV
        event.getRecipients().clear();
        event.setFormat("(" + mainChannel.getName() + ") %s: %s");

        // Flood filter
        if (Social.get().getConfig().getSettings().getChat().getFilter().isFloodFilter()) {
            int floodFilterCooldownInSeconds = Social.get().getConfig().getSettings().getChat().getFilter().getFloodFilterCooldownInMilliseconds();

            if (System.currentTimeMillis() - socialPlayer.getLatestMessageInMilliseconds() < floodFilterCooldownInSeconds &&
                    !socialPlayer.getPlayer().hasPermission("social.filter.bypass")) {

                Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getErrors().getTypingTooFast(), Social.get().getConfig().getMessages().getChannelType());
                event.setCancelled(true);
                return;
            }
        }

        SocialChatMessageSendEvent socialChatMessageEvent = new SocialChatMessageSendEvent(socialPlayer, mainChannel, event.getMessage());
        Bukkit.getPluginManager().callEvent(socialChatMessageEvent);
        if (!socialChatMessageEvent.isCancelled())
            Social.get().getChatManager().sendChatMessage(socialChatMessageEvent.getSender(), socialChatMessageEvent.getChatChannel(), socialChatMessageEvent.getMessage());
    }

    @EventHandler
    public void onSocialChatMessageSend(SocialChatMessageSendEvent event) {
        // Check if player still has permission to chat in their selected channel
        if (!Social.get().getChatManager().hasPermission(event.getSender(), event.getChatChannel())) {
            ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getSettings().getChat().getDefaultChannel());

            event.getChatChannel().removeMember(event.getSender());
            PluginUtil.runTask(() -> Social.get().getPlayerManager().setMainChannel(event.getSender(), defaultChannel));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSocialChatMessageReceive(SocialChatMessageReceiveEvent event) {
        if (event.getChatChannel().getPermission() == null)
            return;

        // We'll remove the player from this channel if they no longer have the required permission
        if (!Social.get().getChatManager().hasPermission(event.getRecipient(), event.getChatChannel())) {
            ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getSettings().getChat().getDefaultChannel());

            event.getChatChannel().removeMember(event.getRecipient());
            PluginUtil.runTask(() -> Social.get().getPlayerManager().setMainChannel(event.getRecipient(), defaultChannel));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSocialChannelPreSwitch(SocialChannelPreSwitchEvent event) {
        if (!event.getChatChannel().getMembers().contains(event.getSocialPlayer().getUuid()))
            event.getChatChannel().addMember(event.getSocialPlayer().getUuid());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSocialChannelPostSwitch(SocialChannelPostSwitchEvent event) {
        Social.get().getTextProcessor().parseAndSend(event.getSocialPlayer(), Social.get().getConfig().getMessages().getCommands().getChannelChanged(), Social.get().getConfig().getMessages().getChannelType());
    }
}
