package ovh.mythmc.social.api.user;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.SimpleChatChannel;
import ovh.mythmc.social.api.database.SocialDatabase;

public abstract class SocialUserService {

    protected abstract AbstractSocialUser createUserInstance(@NotNull UUID uuid);

    public abstract Collection<AbstractSocialUser> get();

    public void register(@NotNull AbstractSocialUser user) {
        SocialDatabase.get().create(user);
    }

    public AbstractSocialUser register(@NotNull UUID uuid) {
        // Todo: recover data from last session
        String defaultChatChannelName = Social.get().getConfig().getChat().getDefaultChannel();
        ChatChannel defaultHybridChatChannel = Social.get().getChatManager().getDefaultChannel();

        AbstractSocialUser user = createUserInstance(uuid);
        if (user.cachedDisplayName() == null)
            user.setCachedDisplayName("");

        user.setSocialSpy(false);

        if (defaultHybridChatChannel == null) {
            Social.get().getLogger().warn("Default channel '" + defaultChatChannelName + "' is unavailable!");
        } else {
            if (!defaultHybridChatChannel.members().contains(user))
                defaultHybridChatChannel.addMember(user);

            user.setMainChannel(defaultHybridChatChannel);
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
