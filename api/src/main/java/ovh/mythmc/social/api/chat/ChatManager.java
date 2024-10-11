package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.events.chat.SocialChatMessageReceiveEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupCreateEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupDisbandEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupLeaderChangeEvent;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.*;

import static net.kyori.adventure.text.Component.text;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class ChatManager {

    public static final ChatManager instance = new ChatManager();

    private final List<ChatChannel> channels = new ArrayList<>();

    public ChatChannel getChannel(final @NotNull String channelName) {
        for (ChatChannel chatChannel : channels) {
            if (chatChannel.getName().equals(channelName))
                return chatChannel;
        }

        return null;
    }

    public GroupChatChannel getGroupChannelByCode(final int code) {
        ChatChannel chatChannel = getChannel("G-" + code);
        if (chatChannel instanceof GroupChatChannel)
            return (GroupChatChannel) chatChannel;

        return null;
    }

    public GroupChatChannel getGroupChannelByPlayer(final @NotNull UUID uuid) {
        for (ChatChannel channel : getChannels()) {
            if (channel instanceof GroupChatChannel && channel.getMembers().contains(uuid)) {
                return (GroupChatChannel) channel;
            }
        }

        return null;
    }

    public GroupChatChannel getGroupChannelByPlayer(final @NotNull SocialPlayer socialPlayer) {
        return getGroupChannelByPlayer(socialPlayer.getUuid());
    }

    public void setGroupChannelLeader(final @NotNull GroupChatChannel groupChatChannel,
                                      final @NotNull UUID leaderUuid) {
        SocialPlayer previousLeader = Social.get().getPlayerManager().get(groupChatChannel.getLeaderUuid());
        SocialPlayer leader = Social.get().getPlayerManager().get(leaderUuid);

        SocialGroupLeaderChangeEvent socialGroupLeaderChangeEvent = new SocialGroupLeaderChangeEvent(groupChatChannel, previousLeader, leader);
        Bukkit.getPluginManager().callEvent(socialGroupLeaderChangeEvent);
    }

    public boolean exists(final @NotNull String channelName) {
        return getChannel(channelName) != null;
    }

    public boolean registerChatChannel(final @NotNull ChatChannel chatChannel) {
        if (Social.get().getConfig().getSettings().isDebug())
            Social.get().getLogger().info("Registered channel '" + chatChannel.getName() + "'");

        return channels.add(chatChannel);
    }

    public boolean registerGroupChatChannel(final @NotNull UUID leaderUuid) {
        int code = (int) Math.floor(100000 + Math.random() * 900000);
        GroupChatChannel chatChannel = new GroupChatChannel(leaderUuid, code);
        chatChannel.addMember(leaderUuid);

        SocialGroupCreateEvent socialGroupCreateEvent = new SocialGroupCreateEvent(chatChannel);
        Bukkit.getPluginManager().callEvent(socialGroupCreateEvent);
        return registerChatChannel(chatChannel);
    }

    public boolean unregisterChatChannel(final @NotNull ChatChannel chatChannel) {
        if (chatChannel instanceof GroupChatChannel) {
            SocialGroupDisbandEvent socialGroupDisbandEvent = new SocialGroupDisbandEvent((GroupChatChannel) chatChannel);
            Bukkit.getPluginManager().callEvent(socialGroupDisbandEvent);
        }

        if (Social.get().getConfig().getSettings().isDebug())
            Social.get().getLogger().info("Unregistered channel '" + chatChannel.getName() + "'");

        return channels.remove(chatChannel);
    }

    public void assignChannelsToPlayer(final @NotNull SocialPlayer socialPlayer) {
        for (ChatChannel channel : Social.get().getChatManager().getChannels()) {
            if (channel.isJoinByDefault()) {
                if (channel.getPermission() == null || socialPlayer.getPlayer().hasPermission(channel.getPermission()))
                    channel.addMember(socialPlayer.getUuid());
            }
        }
    }

    public boolean hasGroup(final @NotNull UUID uuid) {
        return getGroupChannelByPlayer(uuid) != null;
    }

    public boolean hasGroup(final @NotNull SocialPlayer socialPlayer) {
        return hasGroup(socialPlayer.getUuid());
    }

    public boolean hasPermission(final @NotNull SocialPlayer socialPlayer,
                              final @NotNull ChatChannel chatChannel) {

        if (chatChannel.getPermission() == null)
            return true;

        if (socialPlayer.getPlayer().hasPermission(chatChannel.getPermission()))
            return true;

        return false;
    }

    public void sendChatMessage(final @NotNull SocialPlayer sender,
                                final @NotNull ChatChannel chatChannel,
                                final @NotNull String message) {

        List<UUID> players = new ArrayList<>(List.copyOf(chatChannel.getMembers()));

        // SocialSpy
        for (SocialPlayer socialPlayer : Social.get().getPlayerManager().get()) {
            if (chatChannel.getMembers().contains(socialPlayer.getUuid())) continue;
            if (socialPlayer.isSocialSpy())
                players.add(socialPlayer.getUuid());
        }

        Component channelHoverText = text("");
        if (chatChannel.isShowHoverText()) {
            channelHoverText = Social.get().getTextProcessor().parse(sender, Social.get().getConfig().getSettings().getChat().getChannelHoverText())
                    .appendNewline()
                    .append(Social.get().getTextProcessor().parse(sender, chatChannel.getHoverText()));
        }

        Component textDivider = Social.get().getTextProcessor().parse(sender, " " + chatChannel.getTextDivider() + " ");

        Component nickname = Social.get().getTextProcessor().parse(sender, Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat());

        Component filteredMessage = Social.get().getTextProcessor().parsePlayerInput(sender, message);

        Component chatMessage =
                text("")
                        .append(Social.get().getTextProcessor().parse(sender, chatChannel.getIcon() + " ")
                                .hoverEvent(HoverEvent.showText(channelHoverText))
                                .clickEvent(ClickEvent.runCommand("/social:social channel " + chatChannel.getName()))
                        )
                        .append(nickname.color(chatChannel.getNicknameColor()))
                        .append(textDivider)
                        .append(filteredMessage)
                        .color(chatChannel.getTextColor());

        // Call SocialChatMessageReceiveEvent for each channel member
        Map<SocialPlayer, Component> playerMap = new HashMap<>();
        for (UUID uuid : players) {
            SocialPlayer member = Social.get().getPlayerManager().get(uuid);
            SocialChatMessageReceiveEvent socialChatMessageReceiveEvent = new SocialChatMessageReceiveEvent(sender, member, chatChannel, chatMessage);
            Bukkit.getPluginManager().callEvent(socialChatMessageReceiveEvent);
            if (!socialChatMessageReceiveEvent.isCancelled())
                playerMap.put(member, socialChatMessageReceiveEvent.getMessage());
        }

        playerMap.forEach((s, m) -> Social.get().getTextProcessor().send(s, m, chatChannel.getType()));
        Social.get().getPlayerManager().setLatestMessage(sender, System.currentTimeMillis());
    }

    public void sendPrivateMessage(final @NotNull SocialPlayer sender,
                                   final @NotNull SocialPlayer recipient,
                                   final @NotNull String message) {

        Component prefix = Social.get().getTextProcessor().parse(sender, Social.get().getConfig().getSettings().getCommands().getPrivateMessage().prefix() + " ");
        Component prefixHoverText = Social.get().getTextProcessor().parse(sender, Social.get().getConfig().getSettings().getCommands().getPrivateMessage().hoverText());

        Component senderNickname = Social.get().getTextProcessor().parse(sender, Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat());
        Component senderHoverText = Social.get().getTextProcessor().parse(sender, Social.get().getConfig().getSettings().getChat().getClickableNicknameHoverText());

        Component recipientNickname = Social.get().getTextProcessor().parse(recipient, Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat());
        Component recipientHoverText = Social.get().getTextProcessor().parse(recipient, Social.get().getConfig().getSettings().getChat().getClickableNicknameHoverText());

        Component arrow = Social.get().getTextProcessor().parse(recipient, " " + Social.get().getConfig().getSettings().getCommands().getPrivateMessage().arrow() + " ");

        Component filteredMessage = Social.get().getTextProcessor().parsePlayerInput(sender, message);

        if (!sender.getNickname().equals(sender.getPlayer().getName()))
            senderHoverText = senderHoverText
                    .appendNewline()
                    .append(Social.get().getTextProcessor().parse(sender, Social.get().getConfig().getSettings().getChat().getPlayerAliasWarningHoverText()));

        if (!recipient.getNickname().equals(recipient.getPlayer().getName()))
            recipientHoverText = recipientHoverText
                    .appendNewline()
                    .append(Social.get().getTextProcessor().parse(recipient, Social.get().getConfig().getSettings().getChat().getPlayerAliasWarningHoverText()));

        Component chatMessage = Component.empty()
                .append(prefix
                        .hoverEvent(HoverEvent.showText(prefixHoverText))
                )
                .append(senderNickname
                        .hoverEvent(HoverEvent.showText(senderHoverText))
                )
                .append(arrow)
                .append(recipientNickname
                        .hoverEvent(HoverEvent.showText(recipientHoverText))
                )
                .append(text(": ").color(NamedTextColor.GRAY))
                .append(filteredMessage);

        Collection<SocialPlayer> members = new ArrayList<>();
        members.add(sender);
        members.add(recipient);

        Social.get().getPlayerManager().get().forEach(player -> {
            if (player.isSocialSpy() && !members.contains(player))
                members.add(player);
        });

        Social.get().getTextProcessor().send(members, chatMessage, ChannelType.CHAT);
        Social.get().getPlayerManager().setLatestMessage(sender, System.currentTimeMillis());

        // Send message to console
        SocialAdventureProvider.get().console().sendMessage(chatMessage);
    }

}
