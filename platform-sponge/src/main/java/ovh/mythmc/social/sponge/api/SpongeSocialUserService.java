package ovh.mythmc.social.sponge.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.user.SocialUserService;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpongeSocialUserService extends SocialUserService {

    public static final SpongeSocialUserService instance = new SpongeSocialUserService();

    @Override
    protected AbstractSocialUser createUserInstance(@NotNull UUID uuid) {
        return new SpongeSocialUser(uuid);
    }

    @Override
    public Collection<AbstractSocialUser> get() {
        return Sponge.server().onlinePlayers().stream()
            .map(player -> getByUuid(player.uniqueId()).orElse(null))
            .filter(Objects::nonNull)
            .toList();
    }

}
