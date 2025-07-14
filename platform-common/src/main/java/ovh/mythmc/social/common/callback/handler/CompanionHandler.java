package ovh.mythmc.social.common.callback.handler;

import java.util.Set;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.channel.SocialChannelCreateCallback;
import ovh.mythmc.social.api.callback.channel.SocialChannelDeleteCallback;
import ovh.mythmc.social.api.callback.channel.SocialChannelPostSwitchCallback;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.network.channel.channels.SocialBonjourChannel;
import ovh.mythmc.social.api.network.channel.channels.SocialChannelSwitchChannel;
import ovh.mythmc.social.api.network.channel.channels.SocialChannelsRefreshChannel;
import ovh.mythmc.social.api.network.channel.channels.SocialMessagePreviewChannel;
import ovh.mythmc.social.api.network.payload.payloads.channel.SocialChannelSwitchPayload;
import ovh.mythmc.social.api.network.payload.payloads.message.SocialMessagePreviewPayload;
import ovh.mythmc.social.api.scheduler.SocialScheduler;
import ovh.mythmc.social.api.user.SocialUserCompanion;
import ovh.mythmc.social.common.callback.game.CustomPayloadReceiveCallback;
import ovh.mythmc.social.common.callback.game.UserPresenceCallback;

public final class CompanionHandler implements SocialCallbackHandler {

    private static class IdentifierKeys {

        static final String COMPANION_CHANNEL_CREATE = "social:companion-channel-create";
        static final String COMPANION_CHANNEL_DELETE = "social:companion-channel-delete";
        static final String COMPANION_CHANNEL_SWITCH = "social:companion-channel-switch";

    }

    @Override
    public void register() {
        SocialChannelCreateCallback.INSTANCE.registerHandler(IdentifierKeys.COMPANION_CHANNEL_CREATE, (ctx) -> {
            Social.get().getUserService().get().forEach(user -> {
                if (user == null || user.companion().isEmpty())
                    return;
    
                if (ctx.channel() instanceof GroupChatChannel groupChannel &&
                    !groupChannel.isMember(user))
                    return;
    
                if (Social.get().getChatManager().hasPermission(user, ctx.channel()))
                    user.companion().get().open(ctx.channel());
            }); 
        });

        SocialChannelDeleteCallback.INSTANCE.registerHandler(IdentifierKeys.COMPANION_CHANNEL_DELETE, (ctx) -> {
            Social.get().getUserService().get().forEach(user -> {
                if (user == null || user.companion().isEmpty())
                    return;
    
                if (Social.get().getChatManager().hasPermission(user, ctx.channel())) {
                    user.companion().get().close(ctx.channel());
                    if (user.mainChannel().equals(ctx.channel()))
                        user.companion().get().mainChannel(Social.get().getChatManager().getCachedOrDefault(user));
                }
            });
        });

        SocialChannelPostSwitchCallback.INSTANCE.registerHandler(IdentifierKeys.COMPANION_CHANNEL_SWITCH, (ctx) -> {
            if (ctx.user().companion().isPresent())
                ctx.user().companion().get().mainChannel(ctx.channel());
        });

        UserPresenceCallback.INSTANCE.registerHandler("social:companion-initializer", ctx -> {
            // Initialize companion
            ctx.user().ifPresent(user -> {
                Social.get().getUserManager().disableCompanion(user);
        
                SocialScheduler.get().runAsyncTaskLater(() -> {
                    if (!user.isOnline() || user.companion().isEmpty())
                        return;
        
                    user.companion().get().clear();
                    user.companion().get().refresh();
                    user.companion().get().mainChannel(user.mainChannel());
                }, 15);
            });
        });

        CustomPayloadReceiveCallback.INSTANCE.registerHandler("social:companion-receiver", ctx -> {
            final var user = ctx.user();

            if (ctx.channel() instanceof SocialChannelsRefreshChannel) {
                user.companion().ifPresent(SocialUserCompanion::refresh);
            } else if (ctx.channel() instanceof SocialBonjourChannel) {
                if (Social.get().getConfig().getGeneral().isDebug())
                    Social.get().getLogger().info("Received bonjour message from " + user.name() + "! Companion features will be enabled");

                Social.get().getUserManager().enableCompanion(user);
            } else if (ctx.channel() instanceof SocialChannelSwitchChannel) {
                final ChatChannel channel = ((SocialChannelSwitchPayload) ctx.payload()).channel();

                if (user.companion().isEmpty())
                    return;

                if (channel != null)
                    Social.get().getUserManager().setMainChannel(user, channel, false);
            } else if (ctx.channel() instanceof SocialMessagePreviewChannel) {
                if (user.companion().isEmpty())
                    return;

                SocialScheduler.get().runAsyncTask(() -> {
                    final var filteredMessage = Social.get().getTextProcessor().parsePlayerInput(
                        SocialParserContext.builder(user, ((SocialMessagePreviewPayload) ctx.payload()).message())
                            .build());

                    final var context = new SocialRegisteredMessageContext(0, 0, user, user.mainChannel(), Set.of(user), filteredMessage, "", null, null);
                    final var rendered = Social.get().getChatManager().getRegisteredRenderer(user.rendererClass()).render(user, context);

                    user.companion().get().preview(rendered.prefix().append(rendered.message()));
                });
            }

        });
    }

    @Override
    public void unregister() {
        UserPresenceCallback.INSTANCE.unregisterHandlers("social:companion-initializer");
        CustomPayloadReceiveCallback.INSTANCE.unregisterHandlers("social:companion-receiver");
        SocialChannelCreateCallback.INSTANCE.unregisterHandlers(IdentifierKeys.COMPANION_CHANNEL_CREATE);
        SocialChannelDeleteCallback.INSTANCE.unregisterHandlers(IdentifierKeys.COMPANION_CHANNEL_DELETE);
        SocialChannelPostSwitchCallback.INSTANCE.unregisterHandlers(IdentifierKeys.COMPANION_CHANNEL_SWITCH);
    }
    
}
