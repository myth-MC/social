package ovh.mythmc.social.common.context;

import lombok.AccessLevel;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import ovh.mythmc.social.api.context.SocialContext;
import ovh.mythmc.social.api.players.SocialPlayer;
import lombok.Data;
import lombok.Setter;

@Data
@SuperBuilder
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
public class SocialMenuContext implements SocialContext {
    
    private final SocialPlayer viewer;

    private final SocialPlayer target;

}