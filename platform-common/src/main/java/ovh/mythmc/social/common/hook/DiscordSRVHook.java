package ovh.mythmc.social.common.hook;

import github.scarsz.discordsrv.Debug;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.GameChatMessagePostProcessEvent;
import github.scarsz.discordsrv.dependencies.commons.lang3.StringUtils;
import github.scarsz.discordsrv.hooks.chat.ChatHook;
import github.scarsz.discordsrv.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.message.SocialMessagePrepareCallback;
import ovh.mythmc.social.api.callback.message.SocialMessageSendCallback;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.user.SocialUser;

@RequiredArgsConstructor
public final class DiscordSRVHook implements ChatHook {

    private final JavaPlugin plugin;

    public void registerMessageCallbackHandler() {
        SocialMessageSendCallback.INSTANCE.registerListener("social:discordsrv", (sender, channel, message, messageId, replyId) -> {
            if (DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel.getName()) == null) {
                DiscordSRV.debug(Debug.MINECRAFT_TO_DISCORD, "Tried looking up destination Discord channel for social channel " + channel.getName() + " but none found");
                return;
            }

            String plainMessage = PlainTextComponentSerializer.plainText().serialize(message);
    
            if (StringUtils.isBlank(plainMessage)) {
                DiscordSRV.debug(Debug.MINECRAFT_TO_DISCORD, "Received blank social message, not processing");
                return;
            }
    
            DiscordSRV.getPlugin().processChatMessage(sender.player().get(), plainMessage, channel.getName(), false, null);
        });
    }

    public void unregisterMessageCallbackHandler() {
        SocialMessagePrepareCallback.INSTANCE.unregisterListeners("social:discordsrv");
    }

    @Subscribe // compatibility with Bukkit
    public void onGameChatMessagePostProcess(GameChatMessagePostProcessEvent event) {
        if (event.getTriggeringBukkitEvent() instanceof AsyncPlayerChatEvent asyncPlayerChatEvent) {
            //if (chatEvent.getRecipients().isEmpty())
            event.setCancelled(true);
        }
    }

    @Override
    public void broadcastMessageToChannel(String channel, github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component message) {
        ChatChannel chatChannel = getChannelByCaseInsensitiveName(channel);
        if (chatChannel == null)
            return;

        String miniMessage = MessageUtil.toMiniMessage(message);
        
        // Workaround for placeholders
        SocialParserContext context = SocialParserContext.builder(SocialUser.dummy(chatChannel), net.kyori.adventure.text.Component.empty())
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
