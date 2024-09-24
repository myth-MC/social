package ovh.mythmc.social.common.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;

        UUID uuid = event.getPlayer().getUniqueId();
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);
        if (socialPlayer == null)
            return;

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
            if (System.currentTimeMillis() - socialPlayer.getLatestMessageInMilliseconds() < Social.get().getConfig().getSettings().getFilter().getFloodFilterCooldownInMilliseconds() &&
                    !socialPlayer.getPlayer().hasPermission("social.filter.bypass")) {
                processor.processAndSend(socialPlayer, messages.errors.getTypingTooFast());
                return;
            }
        }

        Social.get().getChatManager().sendChatMessage(socialPlayer, mainChannel, event.getMessage());
    }

}
