package ovh.mythmc.social.common.callback.handler;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.channel.*;
import ovh.mythmc.social.api.callback.message.SocialMessagePrepareCallback;
import ovh.mythmc.social.api.callback.message.SocialMessageReceiveCallback;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.channel.PrivateChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.common.callback.game.UserPresence;
import ovh.mythmc.social.common.callback.game.UserPresenceCallback;

public final class ChatHandler implements SocialCallbackHandler {

    private static class IdentifierKeys {

        static final String PRIVATE_MESSAGE_CHANNEL_INFO = "social:private-message-channel-info";
        static final String PRIVATE_MESSAGE_REPLY_UPDATER = "social:private-message-reply-updater";
        static final String REPLY_CHANNEL_SWITCHER = "social:reply-channel-switcher";
        static final String CHAT_PERMISSION_CHECKER = "social:chat-permission-checker";
        static final String CHANNEL_PREPARE_MEMBER = "social:channel-prepare-member";
        static final String CHANNEL_SWITCH_MESSAGE = "social:channel-switch-message";

    }

    @Override
    public void register() {
        // Private channel open info
        SocialChannelCreateCallback.INSTANCE.registerHandler(IdentifierKeys.PRIVATE_MESSAGE_CHANNEL_INFO, ctx -> {
            if (ctx.channel() instanceof PrivateChatChannel privateChatChannel) {
                final var context = SocialParserContext.builder(privateChatChannel.getParticipant2(), Component.text(Social.get().getConfig().getMessages().getInfo().getUserOpenedPrivateChannel()))
                    .channel(privateChatChannel)
                    .build();

                privateChatChannel.getParticipant1().sendParsableMessage(context);
            }
        });

        SocialMessagePrepareCallback.INSTANCE.registerHandler(IdentifierKeys.PRIVATE_MESSAGE_REPLY_UPDATER, ctx -> {
            if (ctx.channel() instanceof PrivateChatChannel privateChatChannel) {
                final AbstractSocialUser recipient = privateChatChannel.getRecipientForSender(ctx.sender());

                Social.get().getUserManager().setLatestPrivateMessageRecipient(ctx.sender(), recipient);
                Social.get().getUserManager().setLatestPrivateMessageRecipient(recipient, ctx.sender());
            }
        });

        SocialMessagePrepareCallback.INSTANCE.registerHandler(IdentifierKeys.REPLY_CHANNEL_SWITCHER, (ctx) -> {
            if (!Social.get().getChatManager().hasPermission(ctx.sender(), ctx.channel())) {
                final ChatChannel defaultChannel = Social.get().getChatManager().getDefault();

                ctx.channel().removeMember(ctx.sender());

                Social.get().getUserManager().setMainChannel(ctx.sender(), defaultChannel, true);
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
                final var defaultChannel = Social.get().getChatManager().getCachedOrDefault(ctx.sender());

                ctx.channel().removeMember(ctx.sender());
                Social.get().getUserManager().setMainChannel(ctx.sender(), defaultChannel, true);

                ctx.cancelled(true);
                //ctx.channel(defaultChannel);    
            }
        });

        SocialMessageReceiveCallback.INSTANCE.registerHandler(IdentifierKeys.CHAT_PERMISSION_CHECKER, (ctx) -> {
            // Play reply sound
            if (ctx.isReply())
                ctx.sender().playSound(Sound.sound(Key.key("block.stone_button.click_on"), Source.PLAYER, 0.7F, 1.7F));

            if (ctx.channel().permission().isEmpty())
                return;

            // We'll remove the player from this channel if they no longer have the required permission
            if (!Social.get().getChatManager().hasPermission(ctx.recipient(), ctx.channel())) {
                final ChatChannel defaultChannel = Social.get().getChatManager().getDefault();

                ctx.channel().removeMember(ctx.recipient());
                Social.get().getUserManager().setMainChannel(ctx.recipient(), defaultChannel, true);
                ctx.cancelled(true);
            }
        });

        SocialChannelPreSwitchCallback.INSTANCE.registerListener(IdentifierKeys.CHANNEL_PREPARE_MEMBER, (user, channel, informUser, cancelled) -> {
            if (!channel.isMember(user))
                channel.addMember(user);
        });

        SocialChannelPostSwitchCallback.INSTANCE.registerListener(IdentifierKeys.CHANNEL_SWITCH_MESSAGE, (user, informUser, previousChannel, channel) -> {
            if (!informUser) // Don't announce with special channels
                return;

            if (user.companion().isPresent()) // Don't send message to users who use the companion mod
                return;

            if (channel instanceof PrivateChatChannel privateChatChannel) {
                final var context = SocialParserContext.builder(privateChatChannel.getRecipientForSender(user), Component.text(Social.get().getConfig().getMessages().getCommands().getChannelChangedToPrivateMessage()))
                    .channel(channel)
                    .build();

                user.sendParsableMessage(context);
                return;
            }
            
            final var context = SocialParserContext.builder(user, Component.text(Social.get().getConfig().getMessages().getCommands().getChannelChanged()))
                .channel(channel)
                .build();

            Social.get().getTextProcessor().parseAndSend(context);
        });

        UserPresenceCallback.INSTANCE.registerHandler("social:assign-channels", ctx -> {
            if (!ctx.type().equals(UserPresence.Type.JOIN))
                return;

            // Assign channels
            ctx.user().ifPresent(user -> {
                Social.get().getChatManager().assignChannelsToPlayer(user);

                final ChatChannel defaultChannel = Social.get().getChatManager().getCachedOrDefault(user);
                if (defaultChannel == null) {
                    Social.get().getLogger().error("Default channel is unavailable!");
                    return;
                }

                Social.get().getUserManager().setMainChannel(user, defaultChannel, true);
            });
        });

        UserPresenceCallback.INSTANCE.registerHandler("social:clear-channels", ctx -> {
            if (!ctx.type().equals(UserPresence.Type.QUIT))
                return;

            // Remove user from channels
            ctx.user().ifPresent(user -> {
                Social.registries().channels().values().forEach(channel -> {
                    if (channel.isMember(user))
                        channel.removeMember(user);
                });
            });
        });
    }

    @Override
    public void unregister() {
        UserPresenceCallback.INSTANCE.unregisterHandlers(
            "social:assign-channels",
            "social:clear-channels"
        );

        SocialChannelCreateCallback.INSTANCE.unregisterHandlers(IdentifierKeys.PRIVATE_MESSAGE_CHANNEL_INFO);
        SocialMessagePrepareCallback.INSTANCE.unregisterHandlers(IdentifierKeys.PRIVATE_MESSAGE_REPLY_UPDATER);
        SocialMessagePrepareCallback.INSTANCE.unregisterHandlers(IdentifierKeys.REPLY_CHANNEL_SWITCHER);
        SocialMessagePrepareCallback.INSTANCE.unregisterHandlers(IdentifierKeys.CHAT_PERMISSION_CHECKER);
        SocialMessageReceiveCallback.INSTANCE.unregisterHandlers(IdentifierKeys.CHAT_PERMISSION_CHECKER);
        SocialChannelPreSwitchCallback.INSTANCE.unregisterListeners(IdentifierKeys.CHANNEL_PREPARE_MEMBER);
        SocialChannelPostSwitchCallback.INSTANCE.unregisterListeners(IdentifierKeys.CHANNEL_SWITCH_MESSAGE);
    }
    
}
