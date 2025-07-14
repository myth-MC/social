package ovh.mythmc.social.bukkit.hook;

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

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.api.callback.message.SocialMessagePrepareCallback;
import ovh.mythmc.social.api.callback.message.SocialMessageSendCallback;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;
import ovh.mythmc.social.api.util.registry.RegistryKey;

@RequiredArgsConstructor
public final class DiscordSRVHook implements ChatHook {

    private final Plugin plugin;

    public void registerMessageCallbackHandler() {
        SocialMessageSendCallback.INSTANCE.registerListener("social:discordsrv", (sender, channel, message, messageId, replyId, cancelled) -> {
            if (DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel.name()) == null) {
                DiscordSRV.debug(Debug.MINECRAFT_TO_DISCORD, "Tried looking up destination Discord channel for social channel " + channel.name() + " but none found");
                return;
            }

            String plainMessage = PlainTextComponentSerializer.plainText().serialize(message);
    
            if (StringUtils.isBlank(plainMessage)) {
                DiscordSRV.debug(Debug.MINECRAFT_TO_DISCORD, "Received blank social message, not processing");
                return;
            }

            final Player player = BukkitSocialUser.from(sender).player().orElse(null);
            DiscordSRV.getPlugin().processChatMessage(player, plainMessage, channel.name(), cancelled, null);
        });
    }

    public void unregisterMessageCallbackHandler() {
        SocialMessagePrepareCallback.INSTANCE.unregisterListeners("social:discordsrv");
    }

    @Subscribe // compatibility with Bukkit
    public void onGameChatMessagePostProcess(GameChatMessagePostProcessEvent event) {
        if (event.getTriggeringBukkitEvent() instanceof AsyncPlayerChatEvent) {
            //if (chatEvent.getRecipients().isEmpty())
            event.setCancelled(true);
        }
    }

    @Override
    public void broadcastMessageToChannel(String channelName, github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component message) {
        ChatChannel channel = getChannelByCaseInsensitiveName(channelName);
        if (channel == null)
            return;

        String miniMessage = MessageUtil.toMiniMessage(message);
        
        // Workaround for placeholders
        SocialParserContext context = SocialParserContext.builder(BukkitSocialUser.dummy(channel), net.kyori.adventure.text.Component.empty())
            .channel(channel)
            .build();

        TextComponent channelIcon =  (TextComponent) Social.get().getTextProcessor().getIdentifiedParser(SocialContextualPlaceholder.class, "channel_icon").get().get(context);

        miniMessage = miniMessage.replace("%channel%", channelIcon.content());

        String finalMiniMessage = miniMessage;
        channel.members().forEach(user -> {
            if (!Social.get().getChatManager().hasPermission(user, channel))
                return;

            // Parsing the message before sending it allows emojis to be shown (necessary for channel icon)
            Social.get().getTextProcessor().parseAndSend(user, user.mainChannel(), finalMiniMessage, ChatChannel.ChannelType.CHAT);
        });
    }

    private static ChatChannel getChannelByCaseInsensitiveName(String name) {
        final var channelRegistry = Social.registries().channels();

        if (!channelRegistry.containsKey(RegistryKey.identified(name))) {
            DiscordSRV.debug(Debug.MINECRAFT_TO_DISCORD, "No channel matching name " + name + " has been found.");
            return null;
        }

        return channelRegistry.value(RegistryKey.identified(name)).orElse(null);
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

}
