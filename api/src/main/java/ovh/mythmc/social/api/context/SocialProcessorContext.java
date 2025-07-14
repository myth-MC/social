package ovh.mythmc.social.api.context;

import java.util.*;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.text.CustomTextProcessor;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
@Experimental
public class SocialProcessorContext extends SocialParserContext {

    @Getter
    private final CustomTextProcessor processor;

    private final List<Class<? extends SocialContextualParser>> appliedParsers;

    SocialProcessorContext(
        AbstractSocialUser user, 
        ChatChannel channel,
        Component message, 
        ChatChannel.ChannelType messageChannelType,
        SocialParserGroup group,
        List<SocialInjectedValue<?>> injectedValues,
        CustomTextProcessor processor) {

        super(user, channel, message, messageChannelType, group, injectedValues);
        this.processor = processor;
        this.appliedParsers = new ArrayList<>();
    }

    @Internal
    public void addAppliedParser(Class<? extends SocialContextualParser> appliedParser) {
        this.appliedParsers.add(appliedParser);
    }

    public List<Class<? extends SocialContextualParser>> appliedParsers() {
        return List.copyOf(appliedParsers);
    }

    public static SocialProcessorContext from(SocialParserContext context, CustomTextProcessor processor) {
        return new SocialProcessorContext(
            context.user(), 
            context.channel(), 
            context.message(), 
            context.messageChannelType(), 
            context.group().orElse(null),
            context.injectedValues(),
            processor);
    }   
    
}
