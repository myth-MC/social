package ovh.mythmc.social.api.context;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Experimental;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialParserContext implements SocialContext {
    
    public final static SocialParserContextBuilder builder(@NotNull SocialUser user, @NotNull Component message) {
        return new SocialParserContextBuilder(user, message);
    }

    private final SocialUser user;

    private final ChatChannel channel;

    private final Component message;

    private final ChannelType messageChannelType;

    private final List<Class<?>> appliedParsers;

    @Experimental private final SocialParserGroup group;

    public static class SocialParserContextBuilder {

        private final SocialUser user;

        private final Component message;

        private ChatChannel channel;

        private ChannelType messageChannelType;

        private List<Class<?>> appliedParsers;

        private SocialParserGroup group;

        private SocialParserContextBuilder(SocialUser user, Component message) {
            this.user = user;
            this.message = message;
            this.channel = user.getMainChannel();
            this.messageChannelType = ChannelType.CHAT;
            this.appliedParsers = new ArrayList<>();
        }

        public SocialParserContext build() {
            return new SocialParserContext(
                this.user, 
                this.channel, 
                this.message, 
                this.messageChannelType, 
                this.appliedParsers, 
                this.group);
        }

        public SocialParserContextBuilder channel(@NotNull ChatChannel channel) {
            this.channel = channel;
            return this;
        }

        public SocialParserContextBuilder messageChannelType(@NotNull ChannelType channelType) {
            this.messageChannelType = channelType;
            return this;
        }

    }
    
}
