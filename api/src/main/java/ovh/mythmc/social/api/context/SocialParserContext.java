package ovh.mythmc.social.api.context;

import java.util.ArrayList;
import java.util.List;

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

@Data
@Builder
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@With
public class SocialParserContext implements SocialContext {

    private final SocialPlayer socialPlayer;

    private final ChatChannel playerChannel;

    private final Component message;

    private final ChannelType messageChannelType;

    @Builder.Default
    private final List<Class<?>> appliedParsers = new ArrayList<>();
    
}
