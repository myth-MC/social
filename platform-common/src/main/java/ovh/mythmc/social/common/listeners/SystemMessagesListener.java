package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.users.SocialUser;

public final class SystemMessagesListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SocialUser user = Social.get().getUserManager().get(event.getPlayer().getUniqueId());
        if (user == null)
            return;

        if (!Social.get().getConfig().getSettings().getSystemMessages().isCustomizeJoinMessage())
            return;

        // Send message to console
        Social.get().getLogger().info(ChatColor.stripColor(event.getJoinMessage().trim()));

        String unformattedMessage = Social.get().getConfig().getSettings().getSystemMessages().getJoinMessage();
        if (unformattedMessage == null || unformattedMessage.isEmpty())
            return;

        Component message = parse(user, user.getMainChannel(), Component.text(unformattedMessage));
        ChannelType channelType = ChannelType.valueOf(Social.get().getConfig().getSettings().getSystemMessages().getChannelType());

        Social.get().getTextProcessor().send(Social.get().getUserManager().get(), message, channelType);

        event.setJoinMessage("");
    }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
       SocialUser user = Social.get().getUserManager().get(event.getPlayer().getUniqueId());
       if (user == null)
           return;

       if (!Social.get().getConfig().getSettings().getSystemMessages().isCustomizeQuitMessage())
           return;

       // Send message to console
       Social.get().getLogger().info(ChatColor.stripColor(event.getQuitMessage().trim()));

       String unformattedMessage = Social.get().getConfig().getSettings().getSystemMessages().getQuitMessage();
       if (unformattedMessage == null || unformattedMessage.isEmpty())
           return;

       Component message = parse(user, user.getMainChannel(), Component.text(unformattedMessage));
       ChannelType channelType = ChannelType.valueOf(Social.get().getConfig().getSettings().getSystemMessages().getChannelType());

       Social.get().getTextProcessor().send(Social.get().getUserManager().get(), message, channelType);

       event.setQuitMessage("");
   }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        SocialUser user = Social.get().getUserManager().get(event.getEntity().getUniqueId());
        if (user == null)
            return;

        if (!Social.get().getConfig().getSettings().getSystemMessages().isCustomizeDeathMessage())
            return;

        String unformattedMessage = Social.get().getConfig().getSettings().getSystemMessages().getDeathMessage();
        if (unformattedMessage == null || unformattedMessage.isEmpty())
            return;

        unformattedMessage = String.format(unformattedMessage, event.getDeathMessage());

        Component deathMessage = parse(user, user.getMainChannel(), Component.text(unformattedMessage));

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (unformattedMessage.contains(player.getName())) {
                SocialUser s = Social.get().getUserManager().get(player.getUniqueId());
                deathMessage = deathMessage.replaceText(TextReplacementConfig
                        .builder()
                        .matchLiteral(player.getName())
                        .replacement(Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat())
                        .build());

                deathMessage = Social.get().getTextProcessor().parse(s, s.getMainChannel(), deathMessage);
            }
        }

        ChannelType channelType = ChannelType.valueOf(Social.get().getConfig().getSettings().getSystemMessages().getChannelType());

        Social.get().getTextProcessor().send(Social.get().getUserManager().get(), deathMessage, channelType);

        // Send message to console
        Social.get().getLogger().info(ChatColor.stripColor(event.getDeathMessage().trim()));

        event.setDeathMessage("");
   }

    private Component parse(SocialUser user, ChatChannel channel, Component message) {
        SocialParserContext context = SocialParserContext.builder()
            .user(user)
            .channel(channel)
            .message(message)
            .messageChannelType(channel.getType())
            .build();

        return Social.get().getTextProcessor().parse(context);
    }

}
