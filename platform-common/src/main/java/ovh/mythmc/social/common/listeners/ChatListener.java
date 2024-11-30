package ovh.mythmc.social.common.listeners;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.events.chat.*;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.util.PluginUtil;

import java.util.UUID;

@RequiredArgsConstructor
public final class ChatListener implements Listener {

    private final JavaPlugin plugin;

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
            Social.get().getTextProcessor().parseAndSend(socialPlayer, mainChannel, Social.get().getConfig().getMessages().getErrors().getUnexpectedError(), Social.get().getConfig().getMessages().getChannelType());
            event.setCancelled(true);
            return;
        }

        if (mainChannel.isPassthrough())
            return;

        // Flood filter
        if (Social.get().getConfig().getSettings().getChat().getFilter().isFloodFilter()) {
            int floodFilterCooldownInSeconds = Social.get().getConfig().getSettings().getChat().getFilter().getFloodFilterCooldownInMilliseconds();

            if (System.currentTimeMillis() - socialPlayer.getLatestMessageInMilliseconds() < floodFilterCooldownInSeconds &&
                    !socialPlayer.getPlayer().hasPermission("social.filter.bypass")) {

                Social.get().getTextProcessor().parseAndSend(socialPlayer, mainChannel, Social.get().getConfig().getMessages().getErrors().getTypingTooFast(), Social.get().getConfig().getMessages().getChannelType());
                event.setCancelled(true);
                return;
            }
        }

        // Check if message is a reply
        Integer replyId = null;
        if (event.getMessage().startsWith("(re:#") && event.getMessage().contains(")")) {
            String replyIdString = event.getMessage().substring(5, event.getMessage().indexOf(")"));
            replyId = tryParse(replyIdString);
            event.setMessage(event.getMessage().replace("(re:#" + replyId + ")", "").trim());
        }

        // Cancel event if message is empty
        if (event.getMessage().isEmpty() || event.getMessage().isBlank()) {
            event.setCancelled(true);
            return;
        }

        // Send chat message
        /*
        SocialChatMessageSendEvent socialChatMessageSendEvent = Social.get().getChatManager().sendChatMessage(socialPlayer, mainChannel, event.getMessage(), replyId);
        if (socialChatMessageSendEvent == null || socialChatMessageSendEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        */
        SocialMessageContext context = Social.get().getChatManager().sendChatMessage(socialPlayer, mainChannel, event.getMessage(), replyId);
        if (context == null) {
            event.setCancelled(true);
            return;
        }

        // This allows the message to be logged in console and sent to plugins such as DiscordSRV
        event.getRecipients().clear();
        event.setFormat("(" + context.chatChannel().getName() + ") %s: %s");
    }

    @EventHandler
    public void onSocialChatMessageSend(SocialChatMessagePrepareEvent event) {
        // Check if player still has permission to chat in their selected channel
        if (!Social.get().getChatManager().hasPermission(event.getSender(), event.getChatChannel())) {
            ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getSettings().getChat().getDefaultChannel());

            event.getChatChannel().removeMember(event.getSender());

            PluginUtil.runGlobalTask(plugin, () -> Social.get().getPlayerManager().setMainChannel(event.getSender(), defaultChannel));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSocialChatMessageReceive(SocialChatMessageReceiveEvent event) {
        // Play reply sound
        if (event.isReply())
            event.getSender().getPlayer().playSound(event.getSender().getPlayer(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.7F, 1.7F);

        if (event.getChatChannel().getPermission() == null)
            return;

        // We'll remove the player from this channel if they no longer have the required permission
        if (!Social.get().getChatManager().hasPermission(event.getRecipient(), event.getChatChannel())) {
            ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getSettings().getChat().getDefaultChannel());

            event.getChatChannel().removeMember(event.getRecipient());
            PluginUtil.runGlobalTask(plugin, () -> Social.get().getPlayerManager().setMainChannel(event.getRecipient(), defaultChannel));
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
        Social.get().getTextProcessor().parseAndSend(event.getSocialPlayer(), event.getChatChannel(), Social.get().getConfig().getMessages().getCommands().getChannelChanged(), Social.get().getConfig().getMessages().getChannelType());
    }

    private Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
