package ovh.mythmc.social.api.channels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.events.groups.SocialGroupAliasChangeEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupCreateEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupDisbandEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupLeaderChangeEvent;
import ovh.mythmc.social.api.players.SocialPlayer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class ChannelManager {

    public static final ChannelManager instance = new ChannelManager();

    private final Collection<ChatChannel> channels = new ArrayList<>();

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
        if (Social.get().getConfig().getSettings().isDebug())
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

        if (Social.get().getConfig().getSettings().isDebug())
            Social.get().getLogger().info("Unregistered channel '" + chatChannel.getName() + "'");

        return channels.remove(chatChannel);
    }

    public Collection<ChatChannel> getVisibleChannels(final @NotNull SocialPlayer socialPlayer) {
        return channels.stream()
            .filter(channel -> hasPermission(socialPlayer, channel))
            .collect(Collectors.toList());
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

    public boolean hasPermission(final @NotNull SocialPlayer socialPlayer, final @NotNull ChatChannel chatChannel) {
        if (chatChannel.getPermission() == null)
            return true;

        if (socialPlayer.getPlayer().hasPermission(chatChannel.getPermission()))
            return true;

        return false;
    }
    
}
