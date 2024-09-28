package ovh.mythmc.social.common.hooks;

import github.scarsz.discordsrv.Debug;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.commons.lang3.StringUtils;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.hooks.chat.ChatHook;
import github.scarsz.discordsrv.util.LangUtil;
import github.scarsz.discordsrv.util.MessageUtil;
import github.scarsz.discordsrv.util.PluginUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.events.chat.SocialChatMessageEvent;
import ovh.mythmc.social.api.players.SocialPlayer;

// Unused for now
public final class DiscordSRVHook implements ChatHook {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMessage(SocialChatMessageEvent event) {
        if (DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(event.getChatChannel().getName()) == null) {
            DiscordSRV.debug(Debug.MINECRAFT_TO_DISCORD, "Tried looking up destination Discord channel for social channel " + event.getChatChannel().getName() + " but none found");
            return;
        }

        if (StringUtils.isBlank(event.getMessage())) {
            DiscordSRV.debug(Debug.MINECRAFT_TO_DISCORD, "Received blank social message, not processing");
            return;
        }

        DiscordSRV.getPlugin().processChatMessage(event.getSocialPlayer().getPlayer(), event.getMessage(), event.getChatChannel().getName(), event.isCancelled(), event);
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

            String legacy = MessageUtil.toLegacy(message);

            String plainMessage = LangUtil.Message.CHAT_CHANNEL_MESSAGE.toString()
                    .replace("%channelcolor%", chatChannel.getColor().asHexString())
                    .replace("%channelname%", chatChannel.getName())
                    .replace("%channelnickname%", chatChannel.getIcon())
                    .replace("%message%", legacy);

            String translatedMessage = MessageUtil.translateLegacy(plainMessage);

            Social.get().getTextProcessor().processAndSend(socialPlayer, translatedMessage, ChannelType.CHAT);
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

}
