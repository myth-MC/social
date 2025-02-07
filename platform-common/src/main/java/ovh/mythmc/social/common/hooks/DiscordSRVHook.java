package ovh.mythmc.social.common.hooks;

import github.scarsz.discordsrv.Debug;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.GameChatMessagePostProcessEvent;
import github.scarsz.discordsrv.dependencies.commons.lang3.StringUtils;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.hooks.chat.ChatHook;
import github.scarsz.discordsrv.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.TextComponent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.events.chat.SocialChatMessagePrepareEvent;
import ovh.mythmc.social.api.users.SocialUser;

@RequiredArgsConstructor
public final class DiscordSRVHook implements ChatHook {

    private final JavaPlugin plugin;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMessage(SocialChatMessagePrepareEvent event) {
        if (DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(event.getChannel().getName()) == null) {
            DiscordSRV.debug(Debug.MINECRAFT_TO_DISCORD, "Tried looking up destination Discord channel for social channel " + event.getChannel().getName() + " but none found");
            return;
        }

        if (StringUtils.isBlank(event.getRawMessage())) {
            DiscordSRV.debug(Debug.MINECRAFT_TO_DISCORD, "Received blank social message, not processing");
            return;
        }

        DiscordSRV.getPlugin().processChatMessage(event.getSender().getPlayer(), event.getRawMessage(), event.getChannel().getName(), event.isCancelled(), event);
    }

    @Subscribe
    public void onGameChatMessagePostProcess(GameChatMessagePostProcessEvent event) {
        if(event.getTriggeringBukkitEvent() instanceof AsyncPlayerChatEvent chatEvent) {
            if (chatEvent.getRecipients().isEmpty())
                event.setCancelled(true);
        }
    }

    @Override
    public void broadcastMessageToChannel(String channel, Component message) {
        ChatChannel chatChannel = getChannelByCaseInsensitiveName(channel);
        if (chatChannel == null)
            return;

        String miniMessage = MessageUtil.toMiniMessage(message);
        
        SocialParserContext context = SocialParserContext.builder()
            .user(new SocialUser.Dummy(chatChannel)) // Workaround for placeholders
            .channel(chatChannel)
            .build();

        TextComponent channelIcon =  (TextComponent) Social.get().getTextProcessor().getContextualPlaceholder("channel_icon").get().get(context);

        miniMessage = miniMessage.replace("%channel%", channelIcon.content());

        String finalMiniMessage = miniMessage;
        chatChannel.getMembers().forEach(user -> {
            if (!Social.get().getChatManager().hasPermission(user, chatChannel))
                return;

            // Parsing the message before sending it allows emojis to be shown (necessary for channel icon)
            Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), finalMiniMessage, ChannelType.CHAT);
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
        return plugin;
    }

}
