package ovh.mythmc.social.api.context;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;

@Data
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SocialPlayerContext extends SocialContext {

    private SocialPlayer socialPlayer;

    private ChatChannel chatChannel;

    @Builder.Default
    private ChannelType channelType = ChannelType.CHAT;
    
}
