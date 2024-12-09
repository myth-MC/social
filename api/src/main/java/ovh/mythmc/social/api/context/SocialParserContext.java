package ovh.mythmc.social.api.context;

import java.util.ArrayList;
import java.util.List;

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
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.group.SocialParserGroup;

@Data
@Builder
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@With
public class SocialParserContext implements SocialContext {

    private final SocialPlayer socialPlayer;

    private final ChatChannel playerChannel;

    private final Component message;

    @Builder.Default
    private final ChannelType messageChannelType = ChannelType.CHAT;

    @Builder.Default private final List<Class<?>> appliedParsers = new ArrayList<>();

    @Experimental private final SocialParserGroup group;

    public ChatChannel playerChannel() {
        if (playerChannel != null)
            return playerChannel;

        return socialPlayer.getMainChannel();
    }
    
}
