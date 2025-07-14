package ovh.mythmc.social.sponge.adventure;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.user.AbstractSocialUser;

public final class SpongeAdventureProvider extends SocialAdventureProvider {

    @Override
    public Audience user(@NotNull AbstractSocialUser user) {
        return Sponge.server().player(user.uuid()).get();
    }

    @Override
    public Audience console() {
        return Sponge.server();
    }

}
