package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.audience.Audience;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;
import ovh.mythmc.social.api.chat.renderer.SocialChatRenderer;
import ovh.mythmc.social.api.chat.renderer.SocialChatRenderer.Registered;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.util.registry.RegistryKey;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class ChatManager {

    public static final ChatManager instance = new ChatManager();

    private final ChatHistory history = new ChatHistory();

    private final Map<Class<?>, SocialChatRenderer.Registered<?>> renderersMap = new HashMap<>();

    private static Collection<? extends ChatChannel> channels() {
        return Social.registries().channels().values();
    }

    public <T> SocialChatRenderer.Registered<T> registerRenderer(final @NotNull Class<T> targetClass, final @NotNull SocialChatRenderer<T> renderer, final @NotNull Function<SocialChatRenderer.Builder<T>, SocialChatRenderer.Builder<T>> options) {
        var builder = SocialChatRenderer.builder(renderer);
        builder = options.apply(builder);

        var registered = builder.build();
        this.renderersMap.put(targetClass, registered);
        return registered;
    }

    public <T> void unregisterRenderer(final @NotNull SocialChatRenderer.Registered<T> registeredRenderer) {
        Map.copyOf(renderersMap).entrySet().stream()
            .filter(entry -> entry.getValue().equals(registeredRenderer))
            .forEach(entry -> this.renderersMap.remove(entry.getKey()));
    }

    public <T> void unregisterAllRenderers(final @NotNull Class<T> targetClass) {
        this.renderersMap.remove(targetClass);
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T> SocialChatRenderer.Registered<T> getRegisteredRenderer(final @NotNull Class<T> object) {
        return (Registered<T>) this.renderersMap.get(object);
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T> SocialChatRenderer.Registered<T> getRegisteredRenderer(final @NotNull Audience audience) {
        return (Registered<T>) this.renderersMap.values().stream()
            .filter(registered -> registered.mapFromAudience(audience).isSuccess())
            .findFirst().orElse(null);
    }

    @Deprecated
    public @Nullable ChatChannel getChannel(final @NotNull String channelName) {
        return Social.registries().channels().value(RegistryKey.identified(channelName)).orElse(null);
    }


    public ChatChannel getDefault() {
        return Social.registries().channels().value(RegistryKey.identified(Social.get().getConfig().getChat().getDefaultChannel())).orElse(null);
    }

    public ChatChannel getCachedOrDefault(@NotNull AbstractSocialUser user) {
        final String cachedMainChannelName = user.cachedMainChannel();
        if (cachedMainChannelName != null) {
            final ChatChannel cachedMainChannel = Social.registries().channels().value(RegistryKey.identified(cachedMainChannelName)).orElse(null);
            if (cachedMainChannel != null)
                return cachedMainChannel;
        }

        return Social.registries().channels().value(RegistryKey.identified(Social.get().getConfig().getChat().getDefaultChannel())).orElse(null);
    }

    @Deprecated
    public GroupChatChannel getGroupChannelByCode(final int code) {
        ChatChannel ChatChannel = getChannel("G-" + code);
        if (ChatChannel instanceof GroupChatChannel)
            return (GroupChatChannel) ChatChannel;

        return null;
    }

    @Deprecated
    public GroupChatChannel getGroupChannelByUser(final @NotNull AbstractSocialUser user) {
        return channels().stream()
            .filter(channel -> channel instanceof GroupChatChannel && channel.members().contains(user))
            .map(channel -> (GroupChatChannel) channel)
            .findFirst().orElse(null);
    }

    @Deprecated
    public boolean exists(final @NotNull String channelName) {
        return getChannel(channelName) != null;
    }

    @Deprecated
    public boolean registerChatChannel(final @NotNull ChatChannel channel) {
        Social.registries().channels().register(RegistryKey.identified(channel.name()), channel);
        return true;
    }

    @Deprecated
    public boolean unregisterChatChannel(final @NotNull ChatChannel channel) {
        final var key = RegistryKey.identified(channel.name());

        if (Social.registries().channels().containsKey(key))
            return Social.registries().channels().unregister(key) != null;

        return false;
    }

    public List<ChatChannel> getVisibleChannels(final @NotNull AbstractSocialUser user) {
        return channels().stream()
            .filter(channel -> hasPermission(user, channel))
            .collect(Collectors.toList());
    }

    public void assignChannelsToPlayer(final @NotNull AbstractSocialUser user) {
        channels().stream()
            .filter(ChatChannel::joinByDefault)
            .filter(channel -> channel.permission().isEmpty() || hasPermission(user, channel))
            .forEach(channel -> channel.addMember(user));
    }

    public boolean hasGroup(final @NotNull AbstractSocialUser user) {
        return getGroupChannelByUser(user) != null;
    }

    public boolean hasPermission(final @NotNull AbstractSocialUser user, final @NotNull ChatChannel channel) {
        if (channel.permission().isEmpty())
            return true;

        return user.checkPermission(channel.permission().get());
    }

}
