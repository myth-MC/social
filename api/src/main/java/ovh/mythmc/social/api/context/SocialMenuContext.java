package ovh.mythmc.social.api.context;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.Accessors;
import ovh.mythmc.social.api.players.SocialPlayer;
import lombok.Data;
import lombok.Setter;
import lombok.With;

@Data
@Builder
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@With
public class SocialMenuContext implements SocialContext {
    
    private final SocialPlayer socialPlayer;

}
