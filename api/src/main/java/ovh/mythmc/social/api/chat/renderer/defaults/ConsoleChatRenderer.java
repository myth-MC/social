package ovh.mythmc.social.api.chat.renderer.defaults;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.renderer.SocialChatRenderer;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.context.SocialRendererContext;
import ovh.mythmc.social.api.user.AbstractSocialUser;

public final class ConsoleChatRenderer implements SocialChatRenderer<Audience> {

    @Override
    public SocialRendererContext render(@NotNull Audience target, @NotNull SocialRegisteredMessageContext context) {
        // Set variables
        final AbstractSocialUser sender = context.sender();
        final ChatChannel channel = context.channel();
        final String rawMessage = context.rawMessage();

        final var prefix = Component.empty()
            .append(Component.text("[" + channel.name() + "]"))
            .appendSpace()
            .append(Component.text(sender.name()))
            .append(Component.text(": "));

        //final var renderedPrefix = Social.get().getTextProcessor().parse(SocialParserContext.builder(sender, prefix).channel(channel).build());

        return new SocialRendererContext(
            sender, 
            channel,
            context.viewers(), 
            prefix,
            rawMessage,
            Component.text(rawMessage),
            context.replyId(),
            context.id()
        );
    }
    
}
