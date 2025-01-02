package ovh.mythmc.social.api.text.parsers;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.users.SocialUser;

import static net.kyori.adventure.text.Component.text;

import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

@Deprecated
@ScheduledForRemoval
public abstract class SocialPlaceholder extends SocialContextualPlaceholder {

    public abstract String process(SocialUser player);

    @Override
    public Component get(SocialParserContext context) {
        return text(process(context.user()));
    }

}
