package ovh.mythmc.social.api.context;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import ovh.mythmc.social.api.players.SocialPlayer;

@Data
@Builder
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@With
public class SocialPrivateMessageContext implements SocialContext {

    private final SocialPlayer sender;

    private final SocialPlayer recipient;

    //private final Collection<SocialPlayer> recipients;

    private final String rawMessage;
    
}
