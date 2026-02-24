package ovh.mythmc.social.api.user;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.chat.channel.SimpleChatChannel;
import ovh.mythmc.social.api.identity.IdentityResolver;

public interface SocialUserService {

    @NotNull
    IdentityResolver identityResolver();

    @NotNull
    Set<SocialUser> get();

    @NotNull
    SocialUser getOrCreate(@NotNull UUID uuid);

    @NotNull
    Optional<SocialUser> getByUuid(@NotNull UUID uuid);

    default Collection<SocialUser> getSocialSpyUsers() {
        return get().stream()
                .filter(user -> user.socialSpy().get())
                .toList();
    }

    default Collection<SocialUser> getSocialSpyUsersInChannel(@NotNull SimpleChatChannel channel) {
        return get().stream()
                .filter(user -> user.socialSpy().get() && user.mainChannel() != null
                        && user.mainChannel().equals(channel))
                .toList();
    }

}
