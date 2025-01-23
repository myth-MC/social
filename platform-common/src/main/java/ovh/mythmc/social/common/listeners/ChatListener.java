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
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.events.chat.*;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.util.PluginUtil;

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;

        UUID uuid = event.getPlayer().getUniqueId();
        SocialUser user = Social.get().getUserManager().get(uuid);
        if (user == null) {
            Social.get().getLogger().error("Unexpected error (missing SocialPlayer)");
            return;
        }

        ChatChannel mainChannel = user.getMainChannel();
        if (mainChannel == null) {
            Social.get().getTextProcessor().parseAndSend(user, mainChannel, Social.get().getConfig().getMessages().getErrors().getUnexpectedError(), Social.get().getConfig().getMessages().getChannelType());
            event.setCancelled(true);
            return;
        }

        if (mainChannel.isPassthrough())
            return;

        // Flood filter
        if (Social.get().getConfig().getSettings().getChat().getFilter().isFloodFilter()) {
            int floodFilterCooldownInSeconds = Social.get().getConfig().getSettings().getChat().getFilter().getFloodFilterCooldownInMilliseconds();

            if (System.currentTimeMillis() - user.getLatestMessageInMilliseconds() < floodFilterCooldownInSeconds &&
                    !user.getPlayer().hasPermission("social.filter.bypass")) {

                Social.get().getTextProcessor().parseAndSend(user, mainChannel, Social.get().getConfig().getMessages().getErrors().getTypingTooFast(), Social.get().getConfig().getMessages().getChannelType());
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
        SocialMessageContext context = Social.get().getChatManager().sendChatMessage(user, mainChannel, event.getMessage(), replyId);
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
        if (!Social.get().getChatManager().hasPermission(event.getSender(), event.getChannel())) {
            ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getSettings().getChat().getDefaultChannel());

            event.getChannel().removeMember(event.getSender());

            PluginUtil.runGlobalTask(plugin, () -> Social.get().getUserManager().setMainChannel(event.getSender(), defaultChannel));
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
            PluginUtil.runGlobalTask(plugin, () -> Social.get().getUserManager().setMainChannel(event.getRecipient(), defaultChannel));
            event.setCancelled(true);
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

    private Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
