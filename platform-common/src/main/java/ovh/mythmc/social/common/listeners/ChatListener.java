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
import ovh.mythmc.social.api.configuration.SocialMessages;
import ovh.mythmc.social.api.events.chat.SocialChannelSwitchEvent;
import ovh.mythmc.social.api.events.chat.SocialChatMessageEvent;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.SocialTextProcessor;

import java.util.UUID;

public final class ChatListener implements Listener {

    private final SocialTextProcessor processor = Social.get().getTextProcessor();
    private final SocialMessages messages = Social.get().getConfig().getMessages();

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

        socialPlayer.setMainChannel(defaultChannel);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (ChatChannel chatChannel : Social.get().getChatManager().getChannels()) {
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
            processor.processAndSend(socialPlayer, messages.errors.getUnexpectedError());
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

                processor.processAndSend(socialPlayer, messages.errors.getTypingTooFast());
                event.setCancelled(true);
                return;
            }
        }

        SocialChatMessageEvent socialChatMessageEvent = new SocialChatMessageEvent(socialPlayer, mainChannel, event.getMessage());
        Bukkit.getPluginManager().callEvent(socialChatMessageEvent);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSocialChatMessage(SocialChatMessageEvent event) {
        if (!event.isCancelled())
            Social.get().getChatManager().sendChatMessage(event.getSocialPlayer(), event.getChatChannel(), event.getMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSocialChannelSwitch(SocialChannelSwitchEvent event) {
        if (!event.getChatChannel().getMembers().contains(event.getSocialPlayer().getUuid()))
            event.getChatChannel().addMember(event.getSocialPlayer().getUuid());

        event.getSocialPlayer().setMainChannel(event.getChatChannel());
        processor.processAndSend(event.getSocialPlayer(), messages.getCommands().getChannelChanged());
    }

}
