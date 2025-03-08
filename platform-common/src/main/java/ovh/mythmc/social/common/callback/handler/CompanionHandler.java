package ovh.mythmc.social.common.callback.handler;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.channel.SocialChannelCreateCallback;
import ovh.mythmc.social.api.callback.channel.SocialChannelDeleteCallback;
import ovh.mythmc.social.api.callback.channel.SocialChannelPostSwitchCallback;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.scheduler.SocialScheduler;
import ovh.mythmc.social.api.user.AbstractSocialUser;
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
                    !groupChannel.getMemberUuids().contains(user.uuid()))
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
                        user.companion().get().mainChannel(Social.get().getChatManager().getDefaultChannel());
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
            final String payloadChannel = ctx.channel();
            final byte[] payload = ctx.payload();

            switch (payloadChannel) {
                case "social:refresh" -> {
                    user.companion().ifPresent(SocialUserCompanion::refresh);
                }
                case "social:bonjour" -> {
                    if (Social.get().getConfig().getGeneral().isDebug())
                        Social.get().getLogger().info("Received bonjour message from " + user.name() + "! Companion features will be enabled");

                    Social.get().getUserManager().enableCompanion(user);
                }
                case "social:switch" -> {
                    if (user.companion().isEmpty())
                        return;

                    ChatChannel channel = Social.get().getChatManager().getChannel(new String(payload));
                    if (channel != null)
                        Social.get().getUserManager().setMainChannel(user, channel);
                }
                case "social:preview" -> {
                    if (user.companion().isEmpty())
                        return;

                    CompletableFuture.runAsync(() -> {
                        final var filteredMessage = Social.get().getTextProcessor().parsePlayerInput(
                            SocialParserContext.builder(user, Component.text(new String(payload)))
                                .build());
    
                        final var context = new SocialRegisteredMessageContext(0, 0, user, user.mainChannel(), Set.of(user), filteredMessage, "", null, null);
                        final var rendered = Social.get().getChatManager().getRegisteredRenderer(AbstractSocialUser.class).render(AbstractSocialUser.dummy(user.mainChannel()), context);
    
                        user.companion().get().preview(rendered.prefix().append(rendered.message()));
                    });
              
                }
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
