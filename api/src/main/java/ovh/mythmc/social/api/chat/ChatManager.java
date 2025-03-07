package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.callback.channel.SocialChannelCreate;
import ovh.mythmc.social.api.callback.channel.SocialChannelCreateCallback;
import ovh.mythmc.social.api.callback.channel.SocialChannelDelete;
import ovh.mythmc.social.api.callback.channel.SocialChannelDeleteCallback;
import ovh.mythmc.social.api.callback.group.SocialGroupAliasChange;
import ovh.mythmc.social.api.callback.group.SocialGroupAliasChangeCallback;
import ovh.mythmc.social.api.callback.group.SocialGroupCreate;
import ovh.mythmc.social.api.callback.group.SocialGroupCreateCallback;
import ovh.mythmc.social.api.callback.group.SocialGroupDisband;
import ovh.mythmc.social.api.callback.group.SocialGroupDisbandCallback;
import ovh.mythmc.social.api.callback.group.SocialGroupLeaderChange;
import ovh.mythmc.social.api.callback.group.SocialGroupLeaderChangeCallback;
import ovh.mythmc.social.api.chat.renderer.SocialChatRenderer;
import ovh.mythmc.social.api.chat.renderer.SocialChatRenderer.Registered;
import ovh.mythmc.social.api.chat.renderer.SocialChatRendererUtil;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.user.AbstractSocialUser;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class ChatManager {

    public static final ChatManager instance = new ChatManager();

    private final ChatHistory history = new ChatHistory();

    private final Collection<ChatChannel> channels = new ArrayList<>();

    private final Map<Class<? extends Object>, SocialChatRenderer.Registered<? extends Object>> renderersMap = new HashMap<>();

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
        return (Registered<T>) this.renderersMap.entrySet().stream()
            .filter(entry -> entry.getValue().mapFromAudience(audience).isSuccess())
            .map(entry -> entry.getValue())
            .findFirst().orElse(null);
    }

    public ChatChannel getChannel(final @NotNull String channelName) {
        return channels.stream()
            .filter(channel -> channel.getName().equals(channelName))
            .findFirst().orElse(null);
    }

    public @Nullable ChatChannel getDefaultChannel() {
        return getChannel(Social.get().getConfig().getChat().getDefaultChannel());
    }

    public GroupChatChannel getGroupChannelByCode(final int code) {
        ChatChannel chatChannel = getChannel("G-" + code);
        if (chatChannel instanceof GroupChatChannel)
            return (GroupChatChannel) chatChannel;

        return null;
    }

    public GroupChatChannel getGroupChannelByUser(final @NotNull UUID uuid) {
        return channels.stream()
            .filter(channel -> channel instanceof GroupChatChannel && channel.getMemberUuids().contains(uuid))
            .map(channel -> (GroupChatChannel) channel)
            .findFirst().orElse(null);
    }

    public GroupChatChannel getGroupChannelByUser(final @NotNull AbstractSocialUser user) {
        return getGroupChannelByUser(user.uuid());
    }

    public void setGroupChannelLeader(final @NotNull GroupChatChannel groupChatChannel,
                                      final @NotNull UUID leaderUuid) {
        AbstractSocialUser previousLeader = Social.get().getUserService().getByUuid(groupChatChannel.getLeaderUuid()).orElse(null);
        AbstractSocialUser leader = Social.get().getUserService().getByUuid(leaderUuid).orElse(null);

        SocialGroupLeaderChange socialGroupLeaderChange = new SocialGroupLeaderChange(groupChatChannel, previousLeader, leader);
        SocialGroupLeaderChangeCallback.INSTANCE.invoke(socialGroupLeaderChange);

        groupChatChannel.setLeaderUuid(leaderUuid);
    }

    public void setGroupChannelAlias(final @NotNull GroupChatChannel groupChatChannel, final @Nullable String alias) {
        var callback = new SocialGroupAliasChange(groupChatChannel, alias);
        SocialGroupAliasChangeCallback.INSTANCE.invoke(callback);
        
        groupChatChannel.setAlias(callback.newAlias());
    }

    public boolean exists(final @NotNull String channelName) {
        return getChannel(channelName) != null;
    }

    public boolean registerChatChannel(final @NotNull ChatChannel chatChannel) {
        if (Social.get().getConfig().getGeneral().isDebug())
            Social.get().getLogger().info("Registered channel '" + chatChannel.getName() + "'");

        var callback = new SocialChannelCreate(chatChannel);
        SocialChannelCreateCallback.INSTANCE.invoke(callback);

        return channels.add(chatChannel);
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

    public boolean unregisterChatChannel(final @NotNull ChatChannel chatChannel) {
        if (Social.get().getConfig().getGeneral().isDebug())
            Social.get().getLogger().info("Unregistered channel '" + chatChannel.getName() + "'");

        if (chatChannel instanceof GroupChatChannel groupChatChannel) {
            var callback = new SocialGroupDisband(groupChatChannel);
            SocialGroupDisbandCallback.INSTANCE.invoke(callback);
        } else {
            var callback = new SocialChannelDelete(chatChannel);
            SocialChannelDeleteCallback.INSTANCE.invoke(callback);
        }

        return channels.remove(chatChannel);
    }

    public List<ChatChannel> getVisibleChannels(final @NotNull AbstractSocialUser user) {
        return channels.stream()
            .filter(channel -> hasPermission(user, channel))
            .collect(Collectors.toList());
    }

    public void assignChannelsToPlayer(final @NotNull AbstractSocialUser user) {
        channels.stream()
            .filter(ChatChannel::isJoinByDefault)
            .filter(channel -> channel.getPermission() == null || hasPermission(user, channel))
            .forEach(channel -> channel.addMember(user));
    }

    public boolean hasGroup(final @NotNull UUID uuid) {
        return getGroupChannelByUser(uuid) != null;
    }

    public boolean hasGroup(final @NotNull AbstractSocialUser user) {
        return hasGroup(user.uuid());
    }

    public boolean hasPermission(final @NotNull AbstractSocialUser user, final @NotNull ChatChannel chatChannel) {
        if (chatChannel.getPermission() == null)
            return true;

        if (user.checkPermission(chatChannel.getPermission()))
            return true;

        return false;
    }

    @Internal
    public void sendPrivateMessage(final @NotNull AbstractSocialUser sender,
                                   final @NotNull AbstractSocialUser recipient,
                                   final @NotNull String message) {

        // Flood filter
        if (Social.get().getConfig().getChat().getFilter().isFloodFilter()) {
            int floodFilterCooldownInMilliseconds = Social.get().getConfig().getChat().getFilter().getFloodFilterCooldownInMilliseconds();

            if (System.currentTimeMillis() - sender.latestMessageInMilliseconds() < floodFilterCooldownInMilliseconds &&
                    !sender.checkPermission("social.filter.bypass")) {

                Social.get().getTextProcessor().parseAndSend(sender, sender.mainChannel(), Social.get().getConfig().getMessages().getErrors().getTypingTooFast(), Social.get().getConfig().getMessages().getChannelType());
                return;
            }
        }

        Component prefix = parse(sender, sender.mainChannel(), Social.get().getConfig().getCommands().getPrivateMessage().prefix() + " ");
        Component prefixHoverText = parse(sender, sender.mainChannel(), Social.get().getConfig().getCommands().getPrivateMessage().hoverText());

        Component senderNickname = parse(sender, sender.mainChannel(), Social.get().getConfig().getChat().getPlayerNicknameFormat());
        Component senderHoverText = parse(sender, sender.mainChannel(), Social.get().getConfig().getChat().getClickableNicknameHoverText());

        Component recipientNickname = parse(recipient, recipient.mainChannel(), Social.get().getConfig().getChat().getPlayerNicknameFormat());
        Component recipientHoverText = parse(recipient, recipient.mainChannel(), Social.get().getConfig().getChat().getClickableNicknameHoverText());

        Component arrow = parse(recipient, recipient.mainChannel(), " " + Social.get().getConfig().getCommands().getPrivateMessage().arrow() + " ");

        Component filteredMessage = parsePlayerInput(sender, sender.mainChannel(), message);

        if (!sender.cachedDisplayName().equals(sender.name()))
            senderHoverText = senderHoverText
                    .appendNewline()
                    .append(parse(sender, sender.mainChannel(), Social.get().getConfig().getChat().getPlayerAliasWarningHoverText()));

        if (!recipient.cachedDisplayName().equals(recipient.name()))
            recipientHoverText = recipientHoverText
                    .appendNewline()
                    .append(parse(recipient, recipient.mainChannel(), Social.get().getConfig().getChat().getPlayerAliasWarningHoverText()));

        Component chatMessage = Component.empty()
                .append(prefix
                        .hoverEvent(HoverEvent.showText(prefixHoverText))
                )
                .append(SocialChatRendererUtil.trim(senderNickname)
                        .hoverEvent(HoverEvent.showText(senderHoverText))
                )
                .append(arrow)
                .append(SocialChatRendererUtil.trim(recipientNickname)
                        .hoverEvent(HoverEvent.showText(recipientHoverText))
                )
                .append(text(": ").color(NamedTextColor.GRAY))
                .append(filteredMessage);

        Collection<AbstractSocialUser> members = new ArrayList<>();
        members.add(sender);
        members.add(recipient);

        Social.get().getUserService().get().forEach(player -> {
            if (player.socialSpy() && !members.contains(player))
                members.add(player);
        });

        Social.get().getTextProcessor().send(members, chatMessage, ChannelType.CHAT, null);
        Social.get().getUserManager().setLatestMessage(sender, System.currentTimeMillis());

        // Send message to console
        SocialAdventureProvider.get().console().sendMessage(Component.text("[PM] " + sender.cachedDisplayName() + " -> " + recipient.cachedDisplayName() + ": " + message));
    }

    private Component parse(AbstractSocialUser user, ChatChannel channel, Component message) {
        SocialParserContext context = SocialParserContext.builder(user, message)
            .channel(channel)
            .build();

        return Social.get().getTextProcessor().parse(context);
    }

    private Component parse(AbstractSocialUser user, ChatChannel channel, String message) {
        return parse(user, channel, text(message));
    }

    private Component parsePlayerInput(AbstractSocialUser user, ChatChannel channel, String message) {
        SocialParserContext context = SocialParserContext.builder(user, text(message))
            .channel(channel)
            .build();

        return Social.get().getTextProcessor().parsePlayerInput(context);
    }

}
