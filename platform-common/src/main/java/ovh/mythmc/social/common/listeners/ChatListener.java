package ovh.mythmc.social.common.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.configuration.SocialMessages;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.SocialTextProcessor;

import java.util.UUID;

public final class ChatListener implements Listener {

    private final SocialTextProcessor processor = Social.get().getTextProcessor();
    private final SocialMessages messages = Social.get().getConfig().getMessages();

    @EventHandler(priority = EventPriority.LOWEST)
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (ChatChannel chatChannel : Social.get().getChatManager().getChannels()) {
            chatChannel.removeMember(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
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

        if (!Social.get().getConfig().getSettings().getChat().isEnabled())
            return;

        ChatChannel mainChannel = socialPlayer.getMainChannel();
        if (mainChannel == null) {
            processor.processAndSend(socialPlayer, messages.errors.getUnexpectedError());
            return;
        }

        if (mainChannel.isPassthrough())
            return;

        event.setCancelled(true);
        if (Social.get().getConfig().getSettings().getFilter().isFloodFilter()) {
            int floodFilterCooldownInSeconds = Social.get().getConfig().getSettings().getFilter().getFloodFilterCooldownInMilliseconds();
            if (System.currentTimeMillis() - socialPlayer.getLatestMessageInMilliseconds() < floodFilterCooldownInSeconds &&
                    !socialPlayer.getPlayer().hasPermission("social.filter.bypass")) {
                processor.processAndSend(socialPlayer, messages.errors.getTypingTooFast());
                return;
            }
        }

        Social.get().getChatManager().sendChatMessage(socialPlayer, mainChannel, event.getMessage());
    }

}
