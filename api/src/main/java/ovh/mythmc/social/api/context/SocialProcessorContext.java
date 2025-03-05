package ovh.mythmc.social.api.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.text.CustomTextProcessor;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
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
        AbstractSocialUser<? extends Object> user, 
        ChatChannel channel, 
        Component message, 
        ChannelType messageChannelType,
        Optional<SocialParserGroup> group,
        CustomTextProcessor processor) {

        super(user, channel, message, messageChannelType, group);
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
            context.group(),
            processor);
    }   
    
}
