package ovh.mythmc.social.common.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.common.adapter.PlatformAdapter;

@RequiredArgsConstructor
public final class SystemMessagesListener implements Listener {

    private final JavaPlugin plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlatformAdapter.get().runAsyncTaskLater(plugin, () -> {
            SocialUser user = Social.get().getUserManager().getByUuid(event.getPlayer().getUniqueId());
            if (user == null)
                return;

            if (!Social.get().getConfig().getSystemMessages().isCustomizeJoinMessage())
                return;

            // Send message to console
            if (event.getJoinMessage() != null)
                Social.get().getLogger().info(ChatColor.stripColor(event.getJoinMessage().trim()));

            String unformattedMessage = Social.get().getConfig().getSystemMessages().getJoinMessage();
            if (unformattedMessage == null || unformattedMessage.isEmpty())
                return;

            Component message = parse(user, user.getMainChannel(), Component.text(unformattedMessage));
            ChannelType channelType = ChannelType.valueOf(Social.get().getConfig().getSystemMessages().getChannelType());

            Social.get().getTextProcessor().send(Social.get().getUserManager().get(), message, channelType, null);
        }, Social.get().getConfig().getSystemMessages().getJoinMessageDelayInTicks());

        event.setJoinMessage("");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        SocialUser user = Social.get().getUserManager().getByUuid(event.getPlayer().getUniqueId());
        if (user == null)
            return;

        if (!Social.get().getConfig().getSystemMessages().isCustomizeQuitMessage())
            return;

        // Send message to console
        if (event.getQuitMessage() != null)
            Social.get().getLogger().info(ChatColor.stripColor(event.getQuitMessage().trim()));

        String unformattedMessage = Social.get().getConfig().getSystemMessages().getQuitMessage();
        if (unformattedMessage == null || unformattedMessage.isEmpty())
            return;

        Component message = parse(user, user.getMainChannel(), Component.text(unformattedMessage));
        ChannelType channelType = ChannelType.valueOf(Social.get().getConfig().getSystemMessages().getChannelType());

        Social.get().getTextProcessor().send(Social.get().getUserManager().get(), message, channelType, null);

        event.setQuitMessage("");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        SocialUser user = Social.get().getUserManager().getByUuid(event.getEntity().getUniqueId());
        if (user == null)
            return;

        if (!Social.get().getConfig().getSystemMessages().isCustomizeDeathMessage())
            return;

        String unformattedMessage = Social.get().getConfig().getSystemMessages().getDeathMessage();
        if (unformattedMessage == null || unformattedMessage.isEmpty())
            return;

        unformattedMessage = String.format(unformattedMessage, event.getDeathMessage());

        Component deathMessage = parse(user, user.getMainChannel(), Component.text(unformattedMessage));

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (unformattedMessage.contains(player.getName())) {
                SocialUser s = Social.get().getUserManager().getByUuid(player.getUniqueId());
                deathMessage = deathMessage.replaceText(TextReplacementConfig
                        .builder()
                        .matchLiteral(player.getName())
                        .replacement(Social.get().getConfig().getChat().getPlayerNicknameFormat())
                        .build());

                deathMessage = Social.get().getTextProcessor().parse(s, s.getMainChannel(), deathMessage);
            }
        }

        ChannelType channelType = ChannelType.valueOf(Social.get().getConfig().getSystemMessages().getChannelType());

        Social.get().getTextProcessor().send(Social.get().getUserManager().get(), deathMessage, channelType, null);

        // Send message to console
        Social.get().getLogger().info(ChatColor.stripColor(event.getDeathMessage().trim()));

        event.setDeathMessage("");
   }

    private Component parse(SocialUser user, ChatChannel channel, Component message) {
        SocialParserContext context = SocialParserContext.builder(user, message)
            .channel(channel)
            .build();

        return Social.get().getTextProcessor().parse(context);
    }

}
