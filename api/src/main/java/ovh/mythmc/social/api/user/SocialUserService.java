package ovh.mythmc.social.api.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.database.SocialDatabase;

public abstract class SocialUserService<P, U extends AbstractSocialUser<P>> {

    private static SocialUserService<?, ?> instance;

    @SuppressWarnings("unchecked")
    public final static <P, U extends AbstractSocialUser<P>> SocialUserService<P, U> of(P playerType) {
        if (instance.canMap(playerType))
            return (SocialUserService<P, U>) instance;

        return null;
    }

    public final static <P, U extends AbstractSocialUser<P>> void set(SocialUserService<P, U> service) {
        instance = service;
    }

    protected abstract U createInstance(@NotNull P player, @NotNull UUID uuid);

    public abstract boolean canMap(Object player);

    public abstract U map(Object player);

    private final Collection<U> users = new ArrayList<>();

    public void register(@NotNull U user) {
        final var databaseUser = DatabaseUser.fromUser(user);
        SocialDatabase.get().create(databaseUser);
    }

    public U register(@NotNull P player, @NotNull UUID uuid) {
        // Todo: recover data from last session
        String defaultChatChannelName = Social.get().getConfig().getChat().getDefaultChannel();
        ChatChannel defaultChatChannel = Social.get().getChatManager().getDefaultChannel();

        U user = createInstance(player, uuid);
        user.setSocialSpy(false);

        if (defaultChatChannel == null) {
            Social.get().getLogger().warn("Default channel '" + defaultChatChannelName + "' is unavailable!");
        } else {
            if (!defaultChatChannel.getMemberUuids().contains(uuid))
                defaultChatChannel.addMember(uuid);

            user.setMainChannel(defaultChatChannel);
        }

        register(user);
        return user;
    }

    public Collection<U> get() {
        return List.copyOf(users);
    }

    public Optional<U> getByUuid(@NotNull UUID uuid) {
        return get().stream()
            .filter(user -> user.uuid().equals(uuid))
            .findFirst();
    }

    public Collection<U> getSocialSpyUsers() {
        return get().stream()
            .filter(SocialUser::socialSpy)
            .toList();
    }

    public Collection<U> getSocialSpyUsersInChannel(ChatChannel channel) {
        return get().stream()
            .filter(user -> user.socialSpy() && user.mainChannel().equals(channel))
            .toList();
    }
    
}
