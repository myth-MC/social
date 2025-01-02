package ovh.mythmc.social.api.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.ApiStatus.Experimental;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.users.SocialUser;

@Data
@Builder
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@With
public class SocialParserContext implements SocialContext {

    private final SocialUser user;

    private final ChatChannel channel;

    private final Component message;

    @Builder.Default
    private final ChannelType messageChannelType = ChannelType.CHAT;

    @Builder.Default private final List<Class<?>> appliedParsers = new ArrayList<>();

    @Experimental private final SocialParserGroup group;

    public ChatChannel channel() {
        return Optional.ofNullable(channel).orElse(user.getMainChannel());
    }

    public static class SocialParserContextBuilder {
        @SuppressWarnings("unused")
        private SocialParserContextBuilder group(SocialParserGroup group) { return this; }
    }
    
}
