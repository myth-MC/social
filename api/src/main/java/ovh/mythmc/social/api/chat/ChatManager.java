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
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.api.util.registry.RegistryKey;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Manages chat channels, renderers and the per-session chat history.
 *
 * <p>
 * Access the singleton via
 * {@link ovh.mythmc.social.api.Social#getChatManager()}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class ChatManager {

    public static final ChatManager instance = new ChatManager();

    private final ChatHistory history = new ChatHistory();

    private final Map<Class<?>, SocialChatRenderer.Registered<?>> renderersMap = new HashMap<>();

    private static Collection<? extends ChatChannel> channels() {
        return Social.registries().channels().values();
    }

    /**
     * Registers a {@link SocialChatRenderer} for a given target class and returns
     * the
     * {@link SocialChatRenderer.Registered} wrapper.
     *
     * @param targetClass the class this renderer maps audience objects from
     * @param renderer    the renderer implementation
     * @param options     a builder customiser for additional options such as
     *                    conditions
     * @param <T>         the audience target type
     * @return the registered renderer handle
     */
    public <T> SocialChatRenderer.Registered<T> registerRenderer(final @NotNull Class<T> targetClass,
            final @NotNull SocialChatRenderer<T> renderer,
            final @NotNull Function<SocialChatRenderer.Builder<T>, SocialChatRenderer.Builder<T>> options) {
        var builder = SocialChatRenderer.builder(renderer);
        builder = options.apply(builder);

        var registered = builder.build();
        this.renderersMap.put(targetClass, registered);
        return registered;
    }

    /**
     * Unregisters the given renderer.
     *
     * @param registeredRenderer the renderer to unregister
     * @param <T>                the audience target type
     */
    public <T> void unregisterRenderer(final @NotNull SocialChatRenderer.Registered<T> registeredRenderer) {
        Map.copyOf(renderersMap).entrySet().stream()
                .filter(entry -> entry.getValue().equals(registeredRenderer))
                .forEach(entry -> this.renderersMap.remove(entry.getKey()));
    }

    /**
     * Unregisters all renderers associated with the given target class.
     *
     * @param targetClass the target class whose renderers should be removed
     * @param <T>         the audience target type
     */
    public <T> void unregisterAllRenderers(final @NotNull Class<T> targetClass) {
        this.renderersMap.remove(targetClass);
    }

    /**
     * Returns the registered renderer for the given class, or {@code null} if none
     * exists.
     *
     * @param object the class to look up
     * @param <T>    the audience target type
     * @return the registered renderer, or {@code null}
     */
    @SuppressWarnings("unchecked")
    public @Nullable <T> SocialChatRenderer.Registered<T> getRegisteredRenderer(final @NotNull Class<T> object) {
        return (Registered<T>) this.renderersMap.get(object);
    }

    /**
     * Returns the first renderer able to map the given audience object, or
     * {@code null} if none.
     *
     * @param audience the audience to match
     * @param <T>      the audience target type
     * @return the matching registered renderer, or {@code null}
     */
    @SuppressWarnings("unchecked")
    public @Nullable <T> SocialChatRenderer.Registered<T> getRegisteredRenderer(final @NotNull Audience audience) {
        return (Registered<T>) this.renderersMap.values().stream()
                .filter(registered -> registered.mapFromAudience(audience).isSuccess())
                .findFirst().orElse(null);
    }

    /** Returns the default channel as configured in {@code settings.yml}. */
    public ChatChannel getDefault() {
        return Social.registries().channels()
                .value(RegistryKey.identified(Social.get().getConfig().getChat().getDefaultChannel())).orElse(null);
    }

    /**
     * Returns the user's cached main channel, or the default channel if they don't
     * have one.
     *
     * @param user the user to look up
     * @return the resolved main channel
     */
    public ChatChannel getCachedOrDefault(@NotNull SocialUser user) {
        if (user.mainChannel().isPresent())
            return user.mainChannel().get();

        return Social.registries().channels()
                .value(RegistryKey.identified(Social.get().getConfig().getChat().getDefaultChannel())).orElse(null);
    }

    /**
     * Returns the {@link GroupChatChannel} whose name matches {@code "G-" + code},
     * if any.
     *
     * @param code the numeric group code
     * @return an optional containing the group channel
     */
    public Optional<GroupChatChannel> groupChannelByCode(final int code) {
        return Social.registries().channels().valuesByType(GroupChatChannel.class).stream()
                .filter(groupChatChannel -> groupChatChannel.name().equals("G-" + code))
                .findAny();
    }

    /**
     * Returns the {@link GroupChatChannel} the given user is currently a member of,
     * if any.
     *
     * @param user the user to look up
     * @return an optional containing the user's group channel
     */
    public Optional<GroupChatChannel> groupChannelByUser(final @NotNull SocialUser user) {
        return Social.registries().channels().valuesByType(GroupChatChannel.class).stream()
                .filter(channel -> channel.isMember(user))
                .findAny();
    }

    /**
     * Returns all channels the given user has permission to see.
     *
     * @param user the user to filter channels for
     * @return the list of visible channels
     */
    public List<ChatChannel> getVisibleChannels(final @NotNull SocialUser user) {
        return channels().stream()
                .filter(channel -> hasPermission(user, channel))
                .collect(Collectors.toList());
    }

    /**
     * Adds the user to every channel that has {@link ChatChannel#joinByDefault()}
     * set and for
     * which they have the required permission.
     *
     * @param user the user to assign channels to
     */
    public void assignChannelsToPlayer(final @NotNull SocialUser user) {
        channels().stream()
                .filter(ChatChannel::joinByDefault)
                .filter(channel -> channel.permission().isEmpty() || hasPermission(user, channel))
                .forEach(channel -> channel.addMember(user));
    }

    /**
     * Returns {@code true} if the given user is a member of any group channel.
     *
     * @param user the user to check
     * @return {@code true} if the user is in a group
     */
    public boolean hasGroup(final @NotNull SocialUser user) {
        return groupChannelByUser(user).isPresent();
    }

    /**
     * Returns {@code true} if the user satisfies the channel's permission
     * requirement
     * (or the channel has no permission requirement).
     *
     * @param user    the user to check
     * @param channel the channel to check against
     * @return {@code true} if the user has access
     */
    public boolean hasPermission(final @NotNull SocialUser user, final @NotNull ChatChannel channel) {
        if (channel.permission().isEmpty())
            return true;

        return user.checkPermission(channel.permission().get());
    }

}
