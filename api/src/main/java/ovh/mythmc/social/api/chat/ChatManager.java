package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.chat.renderer.BaseChatRenderer;
import ovh.mythmc.social.api.chat.renderer.SocialChatRenderer;
import ovh.mythmc.social.api.chat.renderer.SocialChatRendererUtil;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.events.groups.SocialGroupAliasChangeEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupCreateEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupDisbandEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupLeaderChangeEvent;
import ovh.mythmc.social.api.users.SocialUser;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import static net.kyori.adventure.text.Component.text;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class ChatManager {

    public static final ChatManager instance = new ChatManager();

    private final ChatHistory history = new ChatHistory();

    private final Collection<ChatChannel> channels = new ArrayList<>();

    @Setter private SocialChatRenderer renderer = new BaseChatRenderer();

    public ChatChannel getChannel(final @NotNull String channelName) {
        for (ChatChannel chatChannel : channels) {
            if (chatChannel.getName().equals(channelName))
                return chatChannel;
        }

        return null;
    }

    public @Nullable ChatChannel getDefaultChannel() {
        return getChannel(Social.get().getConfig().getSettings().getChat().getDefaultChannel());
    }

    public GroupChatChannel getGroupChannelByCode(final int code) {
        ChatChannel chatChannel = getChannel("G-" + code);
        if (chatChannel instanceof GroupChatChannel)
            return (GroupChatChannel) chatChannel;

        return null;
    }

    public GroupChatChannel getGroupChannelByUser(final @NotNull UUID uuid) {
        for (ChatChannel channel : getChannels()) {
            if (channel instanceof GroupChatChannel && channel.getMembers().contains(uuid)) {
                return (GroupChatChannel) channel;
            }
        }

        return null;
    }

    public GroupChatChannel getGroupChannelByUser(final @NotNull SocialUser user) {
        return getGroupChannelByUser(user.getUuid());
    }

    public void setGroupChannelLeader(final @NotNull GroupChatChannel groupChatChannel,
                                      final @NotNull UUID leaderUuid) {
        SocialUser previousLeader = Social.get().getUserManager().get(groupChatChannel.getLeaderUuid());
        SocialUser leader = Social.get().getUserManager().get(leaderUuid);

        SocialGroupLeaderChangeEvent socialGroupLeaderChangeEvent = new SocialGroupLeaderChangeEvent(groupChatChannel, previousLeader, leader);
        Bukkit.getPluginManager().callEvent(socialGroupLeaderChangeEvent);

        groupChatChannel.setLeaderUuid(leaderUuid);
    }

    public void setGroupChannelAlias(final @NotNull GroupChatChannel groupChatChannel, final @Nullable String alias) {
        SocialGroupAliasChangeEvent socialGroupAliasChangeEvent = new SocialGroupAliasChangeEvent(groupChatChannel, alias);
        Bukkit.getPluginManager().callEvent(socialGroupAliasChangeEvent);
        
        groupChatChannel.setAlias(socialGroupAliasChangeEvent.getAlias());
    }

    public boolean exists(final @NotNull String channelName) {
        return getChannel(channelName) != null;
    }

    public boolean registerChatChannel(final @NotNull ChatChannel chatChannel) {
        if (Social.get().getConfig().getGeneral().isDebug())
            Social.get().getLogger().info("Registered channel '" + chatChannel.getName() + "'");

        return channels.add(chatChannel);
    }

    public boolean registerGroupChatChannel(final @NotNull UUID leaderUuid, final @Nullable String alias) {
        int code = (int) Math.floor(100000 + Math.random() * 900000);
        GroupChatChannel chatChannel = new GroupChatChannel(leaderUuid, alias, code);
        chatChannel.addMember(leaderUuid);

        SocialGroupCreateEvent socialGroupCreateEvent = new SocialGroupCreateEvent(chatChannel);
        Bukkit.getPluginManager().callEvent(socialGroupCreateEvent);
        return registerChatChannel(chatChannel);
    }

    public boolean registerGroupChatChannel(final @NotNull UUID leaderUuid) {
        return registerGroupChatChannel(leaderUuid, null);
    }

    public boolean unregisterChatChannel(final @NotNull ChatChannel chatChannel) {
        if (chatChannel instanceof GroupChatChannel) {
            SocialGroupDisbandEvent socialGroupDisbandEvent = new SocialGroupDisbandEvent((GroupChatChannel) chatChannel);
            Bukkit.getPluginManager().callEvent(socialGroupDisbandEvent);
        }

        if (Social.get().getConfig().getGeneral().isDebug())
            Social.get().getLogger().info("Unregistered channel '" + chatChannel.getName() + "'");

        return channels.remove(chatChannel);
    }

    public List<ChatChannel> getVisibleChannels(final @NotNull SocialUser user) {
        return channels.stream()
            .filter(channel -> hasPermission(user, channel))
            .collect(Collectors.toList());
    }

    public void assignChannelsToPlayer(final @NotNull SocialUser user) {
        for (ChatChannel channel : Social.get().getChatManager().getChannels()) {
            if (channel.isJoinByDefault()) {
                if (channel.getPermission() == null || user.getPlayer().hasPermission(channel.getPermission()))
                    channel.addMember(user.getUuid());
            }
        }
    }

    public boolean hasGroup(final @NotNull UUID uuid) {
        return getGroupChannelByUser(uuid) != null;
    }

    public boolean hasGroup(final @NotNull SocialUser user) {
        return hasGroup(user.getUuid());
    }

    public boolean hasPermission(final @NotNull SocialUser user, final @NotNull ChatChannel chatChannel) {
        if (chatChannel.getPermission() == null)
            return true;

        if (user.getPlayer().hasPermission(chatChannel.getPermission()))
            return true;

        return false;
    }

    public void sendPrivateMessage(final @NotNull SocialUser sender,
                                   final @NotNull SocialUser recipient,
                                   final @NotNull String message) {

        // Flood filter
        if (Social.get().getConfig().getSettings().getChat().getFilter().isFloodFilter()) {
            int floodFilterCooldownInMilliseconds = Social.get().getConfig().getSettings().getChat().getFilter().getFloodFilterCooldownInMilliseconds();

            if (System.currentTimeMillis() - sender.getLatestMessageInMilliseconds() < floodFilterCooldownInMilliseconds &&
                    !sender.getPlayer().hasPermission("social.filter.bypass")) {

                Social.get().getTextProcessor().parseAndSend(sender, sender.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getTypingTooFast(), Social.get().getConfig().getMessages().getChannelType());
                return;
            }
        }

        Component prefix = parse(sender, sender.getMainChannel(), Social.get().getConfig().getSettings().getCommands().getPrivateMessage().prefix() + " ");
        Component prefixHoverText = parse(sender, sender.getMainChannel(), Social.get().getConfig().getSettings().getCommands().getPrivateMessage().hoverText());

        Component senderNickname = parse(sender, sender.getMainChannel(), Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat());
        Component senderHoverText = parse(sender, sender.getMainChannel(), Social.get().getConfig().getSettings().getChat().getClickableNicknameHoverText());

        Component recipientNickname = parse(recipient, recipient.getMainChannel(), Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat());
        Component recipientHoverText = parse(recipient, recipient.getMainChannel(), Social.get().getConfig().getSettings().getChat().getClickableNicknameHoverText());

        Component arrow = parse(recipient, recipient.getMainChannel(), " " + Social.get().getConfig().getSettings().getCommands().getPrivateMessage().arrow() + " ");

        Component filteredMessage = parsePlayerInput(sender, sender.getMainChannel(), message);

        if (!sender.getNickname().equals(sender.getPlayer().getName()))
            senderHoverText = senderHoverText
                    .appendNewline()
                    .append(parse(sender, sender.getMainChannel(), Social.get().getConfig().getSettings().getChat().getPlayerAliasWarningHoverText()));

        if (!recipient.getNickname().equals(recipient.getPlayer().getName()))
            recipientHoverText = recipientHoverText
                    .appendNewline()
                    .append(parse(recipient, recipient.getMainChannel(), Social.get().getConfig().getSettings().getChat().getPlayerAliasWarningHoverText()));

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

        Collection<SocialUser> members = new ArrayList<>();
        members.add(sender);
        members.add(recipient);

        Social.get().getUserManager().get().forEach(player -> {
            if (player.isSocialSpy() && !members.contains(player))
                members.add(player);
        });

        Social.get().getTextProcessor().send(members, chatMessage, ChannelType.CHAT);
        Social.get().getUserManager().setLatestMessage(sender, System.currentTimeMillis());

        // Send message to console
        SocialAdventureProvider.get().console().sendMessage(Component.text("[PM] " + sender.getNickname() + " -> " + recipient.getNickname() + ": " + message));
    }

    private Component parse(SocialUser user, ChatChannel channel, Component message) {
        SocialParserContext context = SocialParserContext.builder()
            .user(user)
            .channel(channel)
            .message(message)
            .build();

        return Social.get().getTextProcessor().parse(context);
    }

    private Component parse(SocialUser user, ChatChannel channel, String message) {
        return parse(user, channel, text(message));
    }

    private Component parsePlayerInput(SocialUser user, ChatChannel channel, String message) {
        SocialParserContext context = SocialParserContext.builder()
            .user(user)
            .channel(channel)
            .message(text(message))
            .build();

        return Social.get().getTextProcessor().parsePlayerInput(context);
    }

}
