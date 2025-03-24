package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.audience.Audience;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.channel.*;
import ovh.mythmc.social.api.callback.group.SocialGroupCreate;
import ovh.mythmc.social.api.callback.group.SocialGroupCreateCallback;
import ovh.mythmc.social.api.callback.group.SocialGroupDisband;
import ovh.mythmc.social.api.callback.group.SocialGroupDisbandCallback;
import ovh.mythmc.social.api.callback.group.SocialGroupLeaderChange;
import ovh.mythmc.social.api.callback.group.SocialGroupLeaderChangeCallback;
import ovh.mythmc.social.api.chat.renderer.SocialChatRenderer;
import ovh.mythmc.social.api.chat.renderer.SocialChatRenderer.Registered;
import ovh.mythmc.social.api.user.AbstractSocialUser;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class ChatManager {

    public static final ChatManager instance = new ChatManager();

    private final ChatHistory history = new ChatHistory();

    private final Set<ChatChannel> channels = new HashSet<>();

    private final Map<Class<?>, SocialChatRenderer.Registered<?>> renderersMap = new HashMap<>();

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

    public ChatChannel getChannel(final @NotNull String channelName) {
        return channels.stream()
            .filter(channel -> channel.name().equals(channelName))
            .findFirst().orElse(null);
    }

    public @Nullable ChatChannel getDefaultChannel() {
        return getChannel(Social.get().getConfig().getChat().getDefaultChannel());
    }

    public GroupChatChannel getGroupChannelByCode(final int code) {
        ChatChannel ChatChannel = getChannel("G-" + code);
        if (ChatChannel instanceof GroupChatChannel)
            return (GroupChatChannel) ChatChannel;

        return null;
    }

    public GroupChatChannel getGroupChannelByUser(final @NotNull AbstractSocialUser user) {
        return channels.stream()
            .filter(channel -> channel instanceof GroupChatChannel && channel.members().contains(user))
            .map(channel -> (GroupChatChannel) channel)
            .findFirst().orElse(null);
    }

    public void setGroupChannelLeader(final @NotNull GroupChatChannel groupChatChannel,
                                      final @NotNull UUID leaderUuid) {
        AbstractSocialUser previousLeader = Social.get().getUserService().getByUuid(groupChatChannel.getLeaderUuid()).orElse(null);
        AbstractSocialUser leader = Social.get().getUserService().getByUuid(leaderUuid).orElse(null);

        SocialGroupLeaderChange socialGroupLeaderChange = new SocialGroupLeaderChange(groupChatChannel, previousLeader, leader);
        SocialGroupLeaderChangeCallback.INSTANCE.invoke(socialGroupLeaderChange);

        groupChatChannel.setLeaderUuid(leaderUuid);
    }

    public void setChannelAlias(final @NotNull ChatChannel channel, final @Nullable String alias) {
        final var callback = new SocialChannelAliasChange(channel, alias);
        SocialChannelAliasChangeCallback.INSTANCE.invoke(callback);

        channel.alias(callback.newAlias());
    }

    public boolean exists(final @NotNull String channelName) {
        return getChannel(channelName) != null;
    }

    public boolean registerChatChannel(final @NotNull ChatChannel channel) {
        if (Social.get().getConfig().getGeneral().isDebug())
            Social.get().getLogger().info("Registered channel '" + channel.name() + "'");

        final var callback = new SocialChannelCreate(channel);
        SocialChannelCreateCallback.INSTANCE.invoke(callback);

        return channels.add(channel);
    }

    public boolean registerGroupChatChannel(final @NotNull UUID leaderUuid, final @Nullable String alias) {
        int code = (int) Math.floor(100000 + Math.random() * 900000);
        GroupChatChannel chatChannel = new GroupChatChannel(leaderUuid, alias, code);
        chatChannel.addMember(leaderUuid);

        var callback = new SocialGroupCreate(chatChannel);
        SocialGroupCreateCallback.INSTANCE.invoke(callback);

        return registerChatChannel(callback.groupChatChannel());
    }

    public boolean registerGroupChatChannel(final @NotNull UUID leaderUuid) {
        return registerGroupChatChannel(leaderUuid, null);
    }

    public boolean unregisterChatChannel(final @NotNull ChatChannel channel) {
        if (Social.get().getConfig().getGeneral().isDebug())
            Social.get().getLogger().info("Unregistered channel '" + channel.name() + "'");

        if (channel instanceof GroupChatChannel groupChatChannel) {
            final var callback = new SocialGroupDisband(groupChatChannel);
            SocialGroupDisbandCallback.INSTANCE.invoke(callback);
        } else {
            final var callback = new SocialChannelDelete(channel);
            SocialChannelDeleteCallback.INSTANCE.invoke(callback);
        }

        return channels.remove(channel);
    }

    public List<ChatChannel> getVisibleChannels(final @NotNull AbstractSocialUser user) {
        return channels.stream()
            .filter(channel -> hasPermission(user, channel))
            .collect(Collectors.toList());
    }

    public void assignChannelsToPlayer(final @NotNull AbstractSocialUser user) {
        channels.stream()
            .filter(ChatChannel::joinByDefault)
            .filter(channel -> channel.permission() == null || hasPermission(user, channel))
            .forEach(channel -> channel.addMember(user));
    }

    public boolean hasGroup(final @NotNull AbstractSocialUser user) {
        return getGroupChannelByUser(user) != null;
    }

    public boolean hasPermission(final @NotNull AbstractSocialUser user, final @NotNull ChatChannel channel) {
        if (channel.permission() == null)
            return true;

        return user.checkPermission(channel.permission());
    }

    public PrivateChatChannel privateChatChannel(final @NotNull AbstractSocialUser sender, final @NotNull AbstractSocialUser recipient) {
        PrivateChatChannel privateChatChannel = channels.stream()
            .filter(channel -> channel instanceof PrivateChatChannel)
            .map(channel -> (PrivateChatChannel) channel)
            .filter(channel -> channel.members().contains(sender) && channel.members().contains(recipient))
            .findFirst().orElse(null);

        if (privateChatChannel == null) {
            privateChatChannel = new PrivateChatChannel(sender, recipient);
            registerChatChannel(privateChatChannel);
        }

        return privateChatChannel;
    }

}
