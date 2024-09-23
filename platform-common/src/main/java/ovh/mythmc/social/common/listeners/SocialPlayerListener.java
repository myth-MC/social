package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.placeholders.SocialPlaceholder;
import ovh.mythmc.social.api.placeholders.SocialPlaceholderProcessor;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.awt.*;
import java.util.UUID;

public final class SocialPlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);

        if (socialPlayer == null) {
            socialPlayer = new SocialPlayer(uuid);
            Social.get().getPlayerManager().registerSocialPlayer(socialPlayer);
        }

        // Chat-related stuff
        ChatChannel defaultChatChannel = Social.get().getChatManager().getChannel(Social.get().getSettings().get().getChat().getDefaultChannel());

        if (!defaultChatChannel.getMembers().contains(socialPlayer.getUuid()))
            defaultChatChannel.addMember(socialPlayer.getUuid());

        socialPlayer.setMainChannel(defaultChatChannel);
        socialPlayer.setNickname(socialPlayer.getPlayer().getName());
        socialPlayer.setMuted(false);
        socialPlayer.setSocialSpy(false);

        event.setJoinMessage("");
        for (Player player : Bukkit.getOnlinePlayers()) {
            String unformattedMessage = Social.get().getSettings().get().getSystemMessages().getJoinMessage();
            Component message = Social.get().getPlaceholderProcessor().process(socialPlayer, unformattedMessage);
            SocialAdventureProvider.get().sendMessage(player, message);
        }

        // Todo: add player to every channel where they have access
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);

        if (socialPlayer != null)
            Social.get().getPlayerManager().unregisterSocialPlayer(socialPlayer);

        for (ChatChannel chatChannel : Social.get().getChatManager().getChannels()) {
            chatChannel.removeMember(uuid);
        }

        event.setQuitMessage("");
        for (Player player : Bukkit.getOnlinePlayers()) {
            String unformattedMessage = Social.get().getSettings().get().getSystemMessages().getQuitMessage();
            Component message = Social.get().getPlaceholderProcessor().process(socialPlayer, unformattedMessage);
            SocialAdventureProvider.get().sendMessage(player, message);
        }

    }

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

        ChatChannel mainChannel = socialPlayer.getMainChannel();
        if (mainChannel.isPassthrough())
            return;

        event.setCancelled(true);
        Social.get().getChatManager().sendChatMessage(socialPlayer, mainChannel, event.getMessage());
    }

}
