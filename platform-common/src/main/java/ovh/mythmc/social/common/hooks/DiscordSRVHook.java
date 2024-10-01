package ovh.mythmc.social.common.hooks;

import github.scarsz.discordsrv.Debug;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.commons.lang3.StringUtils;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.hooks.chat.ChatHook;
import github.scarsz.discordsrv.util.MessageUtil;
import github.scarsz.discordsrv.util.PluginUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.events.chat.SocialChatMessageSendEvent;
import ovh.mythmc.social.api.hooks.SocialPluginHook;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class DiscordSRVHook extends SocialPluginHook<DiscordSRV> implements ChatHook, Listener {

    // SocialPluginHook
    public DiscordSRVHook(DiscordSRV storedClass) {
        super(storedClass);
        DiscordSRV.getPlugin().getPluginHooks().add(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMessage(SocialChatMessageSendEvent event) {
        if (DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(event.getChatChannel().getName()) == null) {
            DiscordSRV.debug(Debug.MINECRAFT_TO_DISCORD, "Tried looking up destination Discord channel for social channel " + event.getChatChannel().getName() + " but none found");
            return;
        }

        if (StringUtils.isBlank(event.getMessage())) {
            DiscordSRV.debug(Debug.MINECRAFT_TO_DISCORD, "Received blank social message, not processing");
            return;
        }

        DiscordSRV.getPlugin().processChatMessage(event.getSender().getPlayer(), event.getMessage(), event.getChatChannel().getName(), event.isCancelled(), event);
    }

    @Override
    public void broadcastMessageToChannel(String channel, Component message) {
        ChatChannel chatChannel = getChannelByCaseInsensitiveName(channel);
        if (chatChannel == null)
            return;

        chatChannel.getMembers().forEach(uuid -> {
            SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);
            if (socialPlayer == null)
                return;

            String miniMessage = MessageUtil.toMiniMessage(message);
            Social.get().getTextProcessor().parseAndSend(socialPlayer, miniMessage, ChannelType.CHAT);
        });
    }

    private static ChatChannel getChannelByCaseInsensitiveName(String name) {
        if (!Social.get().getChatManager().exists(name)) {
            DiscordSRV.debug(Debug.MINECRAFT_TO_DISCORD, "No channel matching name " + name + " has been found.");
            return null;
        }

        return Social.get().getChatManager().getChannel(name);
    }

    @Override
    public Plugin getPlugin() {
        return PluginUtil.getPlugin("social");
    }

    // SocialPluginHook
    @Override
    public String identifier() {
        return "DiscordSRV";
    }
}
