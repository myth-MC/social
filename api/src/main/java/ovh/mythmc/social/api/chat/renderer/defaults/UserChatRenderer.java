package ovh.mythmc.social.api.chat.renderer.defaults;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.renderer.SocialChatRenderer;
import ovh.mythmc.social.api.chat.renderer.feature.ChatRendererFeature;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.context.SocialRendererContext;
import ovh.mythmc.social.api.user.AbstractSocialUser;

public class UserChatRenderer<U extends AbstractSocialUser> implements SocialChatRenderer<U> {

    @Override
    public SocialRendererContext render(@NotNull U target, @NotNull SocialRegisteredMessageContext context) {
        // Set variables
        final AbstractSocialUser sender = context.sender();
        final ChatChannel channel = context.channel();

        final var formatBuilderContext = SocialParserContext.builder(sender, Component.empty())
            .build();

        final var formattedPrefix = context.channel().prefix(target, context, formatBuilderContext);

        var renderedPrefix = Social.get().getTextProcessor().parse(
            SocialParserContext.builder(sender, formattedPrefix)
                .channel(channel)
                .build());

        var decoratedMessage = context.message();
        for (ChatRendererFeature feature : channel.supportedRendererFeatures()) {
            if (feature.isApplicable(context))
                decoratedMessage = feature.decorator().decorate(context, decoratedMessage);
        }

        return new SocialRendererContext(
            sender, 
            channel,
            context.viewers(), 
            renderedPrefix, 
            context.rawMessage(),
            decoratedMessage,
            context.replyId(),
            context.id()
        );
    }

}
