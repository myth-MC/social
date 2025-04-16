package ovh.mythmc.social.api.user;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.channel.SimpleChatChannel;
import ovh.mythmc.social.api.database.SocialDatabase;
import ovh.mythmc.social.api.database.reference.UUIDResolver;

public abstract class SocialUserService {

    protected abstract AbstractSocialUser createUserInstance(@NotNull UUID uuid);

    public abstract UUIDResolver uuidResolver();

    public abstract Collection<AbstractSocialUser> get();

    public void register(@NotNull AbstractSocialUser user) {
        SocialDatabase.get().create(user);
    }

    public AbstractSocialUser register(@NotNull UUID uuid) {
        final AbstractSocialUser user = createUserInstance(uuid);
        if (user.cachedDisplayName() == null)
            user.setCachedDisplayName("");

        final ChatChannel cachedOrDefaultChannel = Social.get().getChatManager().getCachedOrDefault(user);
        user.setSocialSpy(false);

        if (cachedOrDefaultChannel == null) {
            Social.get().getLogger().warn("Default channel '" + cachedOrDefaultChannel + "' is unavailable!");
        } else {
            if (!cachedOrDefaultChannel.isMember(user))
                cachedOrDefaultChannel.addMember(user);

            user.setMainChannel(cachedOrDefaultChannel);
        }

        register(user);
        return user;
    }

    public Optional<AbstractSocialUser> getByName(@NotNull String name) {
        return Optional.ofNullable(SocialDatabase.get().getUserByName(name));
    }

    public Optional<AbstractSocialUser> getByUuid(@NotNull UUID uuid) {
        return Optional.ofNullable(SocialDatabase.get().getUserByUuid(uuid));
    }

    public Collection<AbstractSocialUser> getSocialSpyUsers() {
        return get().stream()
            .filter(SocialUser::socialSpy)
            .toList();
    }

    public Collection<AbstractSocialUser> getSocialSpyUsersInChannel(SimpleChatChannel channel) {
        return get().stream()
            .filter(user -> user.socialSpy() && user.mainChannel().equals(channel))
            .toList();
    }
    
}
