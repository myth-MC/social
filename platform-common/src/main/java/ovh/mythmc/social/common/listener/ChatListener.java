package ovh.mythmc.social.common.listener;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.channel.SocialChannelPostSwitchCallback;
import ovh.mythmc.social.api.callback.channel.SocialChannelPreSwitchCallback;
import ovh.mythmc.social.api.callback.message.SocialMessagePrepareCallback;
import ovh.mythmc.social.api.callback.message.SocialMessageReceiveCallback;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.common.adapter.PlatformAdapter;
import ovh.mythmc.social.api.context.SocialParserContext;

import java.util.UUID;

@RequiredArgsConstructor
public final class ChatListener implements Listener {

    private final JavaPlugin plugin;

    private static class IdentifierKeys {

        static final String REPLY_CHANNEL_SWITCHER = "social:reply-channel-switcher";
        static final String CHAT_PERMISSION_CHECKER = "social:chat-permission-checker";
        static final String CHANNEL_PREPARE_MEMBER = "social:channel-prepare-member";
        static final String CHANNEL_SWITCH_MESSAGE = "social:channel-switch-message";

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        SocialUser user = Social.get().getUserManager().getByUuid(uuid);
        if (user == null) {
            // unexpected error;
            return;
        }

        Social.get().getChatManager().assignChannelsToPlayer(user);

        ChatChannel defaultChannel = Social.get().getChatManager().getDefaultChannel();
        if (defaultChannel == null) {
            Social.get().getLogger().error("Default channel is unavailable!");
            return;
        }

        Social.get().getUserManager().setMainChannel(user, defaultChannel);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (ChatChannel chatChannel : Social.get().getChatManager().getChannels()) {
            if (chatChannel.getMemberUuids().contains(event.getPlayer().getUniqueId()))
                chatChannel.removeMember(event.getPlayer().getUniqueId());
        }
    }

    public void registerCallbackHandlers() {
        SocialMessagePrepareCallback.INSTANCE.registerHandler(IdentifierKeys.REPLY_CHANNEL_SWITCHER, (ctx) -> {
            if (!Social.get().getChatManager().hasPermission(ctx.sender(), ctx.channel())) {
                ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getChat().getDefaultChannel());

                ctx.channel().removeMember(ctx.sender());

                PlatformAdapter.get().runGlobalTask(plugin, () -> Social.get().getUserManager().setMainChannel(ctx.sender(), defaultChannel));
                ctx.cancelled(true);
                return;
            }

            if (ctx.isReply()) {
                // Get the reply context
                SocialRegisteredMessageContext reply = Social.get().getChatManager().getHistory().getById(ctx.replyId());

                // Chain of replies (thread)
                if (reply.isReply())
                    ctx.replyId(reply.replyId());

                // Switch channel if necessary
                if (!reply.channel().equals(ctx.channel()) && Social.get().getChatManager().hasPermission(ctx.sender(), reply.channel()))
                    ctx.channel(reply.channel());
            }
        });

        SocialMessagePrepareCallback.INSTANCE.registerHandler(IdentifierKeys.CHAT_PERMISSION_CHECKER, ctx -> {
            // We'll remove the player from this channel if they no longer have the required permission
            if (!Social.get().getChatManager().hasPermission(ctx.sender(), ctx.channel())) {
                var defaultChannel = Social.get().getChatManager().getDefaultChannel();

                ctx.channel().removeMember(ctx.sender());
                //PlatformAdapter.get().runGlobalTask(plugin, () -> Social.get().getUserManager().setMainChannel(ctx.sender(), defaultChannel));
                Social.get().getUserManager().setMainChannel(ctx.sender(), defaultChannel);

                ctx.cancelled(true);
                //ctx.channel(defaultChannel);    
            }
        });

        SocialMessageReceiveCallback.INSTANCE.registerHandler(IdentifierKeys.CHAT_PERMISSION_CHECKER, (ctx) -> {
            // Play reply sound
            if (ctx.isReply())
                ctx.sender().player().ifPresent(player -> player.playSound(player, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.7F, 1.7F));

            if (ctx.channel().getPermission() == null)
                return;

            // We'll remove the player from this channel if they no longer have the required permission
            if (!Social.get().getChatManager().hasPermission(ctx.recipient(), ctx.channel())) {
                ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getChat().getDefaultChannel());

                ctx.channel().removeMember(ctx.recipient());
                PlatformAdapter.get().runGlobalTask(plugin, () -> Social.get().getUserManager().setMainChannel(ctx.recipient(), defaultChannel));
                ctx.cancelled(true);
            }
        });

        SocialChannelPreSwitchCallback.INSTANCE.registerListener(IdentifierKeys.CHANNEL_PREPARE_MEMBER, (user, channel, cancelled) -> {
            if (!channel.getMemberUuids().contains(user.getUuid()))
                channel.addMember(user.getUuid());
        });

        SocialChannelPostSwitchCallback.INSTANCE.registerListener(IdentifierKeys.CHANNEL_SWITCH_MESSAGE, (user, previousChannel, channel) -> {
            if (user.companion().isPresent())
                return;
            
            SocialParserContext context = SocialParserContext.builder(user, Component.text(Social.get().getConfig().getMessages().getCommands().getChannelChanged()))
                .channel(channel)
                .build();

            Social.get().getTextProcessor().parseAndSend(context);
        });
    }

    public void unregisterCallbackHandlers() {
        SocialMessagePrepareCallback.INSTANCE.unregisterHandlers(IdentifierKeys.REPLY_CHANNEL_SWITCHER);
        SocialMessagePrepareCallback.INSTANCE.unregisterHandlers(IdentifierKeys.CHAT_PERMISSION_CHECKER);
        SocialMessageReceiveCallback.INSTANCE.unregisterHandlers(IdentifierKeys.CHAT_PERMISSION_CHECKER);
        SocialChannelPreSwitchCallback.INSTANCE.unregisterListeners(IdentifierKeys.CHANNEL_PREPARE_MEMBER);
        SocialChannelPostSwitchCallback.INSTANCE.unregisterListeners(IdentifierKeys.CHANNEL_SWITCH_MESSAGE);
    }

}
