package ovh.mythmc.social.common.callback.handler;

import java.util.Optional;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.scheduler.SocialScheduler;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.common.callback.game.UserDeathCallback;
import ovh.mythmc.social.common.callback.game.UserPresenceCallback;

public final class SystemMessageHandler implements SocialCallbackHandler {

    @Override
    public void register() {
        UserDeathCallback.INSTANCE.registerHandler("social:death-system-message", ctx -> {
            if (!Social.get().getConfig().getSystemMessages().isCustomizeDeathMessage())
                return;

            String unformattedMessage = Social.get().getConfig().getSystemMessages().getDeathMessage();
            if (unformattedMessage == null || unformattedMessage.isEmpty())
                return;
        
            unformattedMessage = String.format(unformattedMessage, "");
        
            Component deathMessage = parse(ctx.user(), ctx.user().mainChannel(), Component.text(unformattedMessage).append(ctx.deathMessage()));
            
            for (AbstractSocialUser<?> user : Social.get().getUserService().get()) {
                if (unformattedMessage.contains(user.name())) {
                    deathMessage = deathMessage.replaceText(TextReplacementConfig.builder()
                        .matchLiteral(user.name())
                        .replacement(Social.get().getConfig().getChat().getPlayerNicknameFormat())
                        .build());

                    deathMessage = Social.get().getTextProcessor().parse(user, user.mainChannel(), deathMessage);
                }
            }

            final ChannelType channelType = ChannelType.valueOf(Social.get().getConfig().getSystemMessages().getChannelType());

            Social.get().getTextProcessor().send(Social.get().getUserService().get(), deathMessage, channelType, null);
            ctx.deathMessage(Component.empty());
        });

        UserPresenceCallback.INSTANCE.registerHandler("social:presence-system-messages", ctx -> {
            switch (ctx.type()) {
                case JOIN -> {
                    if (!Social.get().getConfig().getSystemMessages().isCustomizeJoinMessage())
                        return;
    
                    if (ctx.message().isEmpty())
                        return;

                    // Cancel original message
                    ctx.message(Optional.empty());

                    SocialScheduler.get().runAsyncTaskLater(() -> {
                        ctx.user().ifPresent(user -> {
                            final String unformattedMessage = Social.get().getConfig().getSystemMessages().getJoinMessage();
                            if (unformattedMessage == null || unformattedMessage.isEmpty())
                                return;
            
                            final Component parsedMessage = parse(user, user.mainChannel(), Component.text(unformattedMessage));
                            final ChannelType channelType = ChannelType.valueOf(Social.get().getConfig().getSystemMessages().getChannelType());
            
                            Social.get().getTextProcessor().send(Social.get().getUserService().get(), parsedMessage, channelType, null);
                        });
                    }, Social.get().getConfig().getSystemMessages().getJoinMessageDelayInTicks());
                }
                case QUIT -> {
                    if (!Social.get().getConfig().getSystemMessages().isCustomizeQuitMessage())
                        return;

                    if (ctx.message().isEmpty())
                        return;

                    // Cancel original message
                    ctx.message(Optional.empty());

                    ctx.user().ifPresent(user -> {
                        final String unformattedMessage = Social.get().getConfig().getSystemMessages().getQuitMessage();
                        if (unformattedMessage == null || unformattedMessage.isEmpty())
                            return;
        
                        final Component parsedMessage = parse(user, user.mainChannel(), Component.text(unformattedMessage));
                        final ChannelType channelType = ChannelType.valueOf(Social.get().getConfig().getSystemMessages().getChannelType());
        
                        Social.get().getTextProcessor().send(Social.get().getUserService().get(), parsedMessage, channelType, null);
                    });
                }
                default -> {}
            };
        });
    }

    @Override
    public void unregister() {
        UserPresenceCallback.INSTANCE.unregisterHandlers("social:presence-system-messages");
    }

    private Component parse(AbstractSocialUser<?> user, ChatChannel channel, Component message) {
        SocialParserContext context = SocialParserContext.builder(user, message)
            .channel(channel)
            .build();

        return Social.get().getTextProcessor().parse(context);
    }
    
}
