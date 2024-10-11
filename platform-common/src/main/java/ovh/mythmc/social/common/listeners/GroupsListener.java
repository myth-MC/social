package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.events.groups.SocialGroupDisbandEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupJoinEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupLeaderChangeEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupLeaveEvent;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class GroupsListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGroupJoin(SocialGroupJoinEvent event) {
        Component joinMessage = Social.get().getTextProcessor().parse(event.getPlayer(), Social.get().getConfig().getMessages().getInfo().getPlayerJoinedGroup());

        event.getGroupChatChannel().getMembers().forEach(uuid -> {
            if (uuid.equals(event.getPlayer().getUuid())) return;

            SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);
            if (socialPlayer == null) return;

            Social.get().getTextProcessor().send(socialPlayer, joinMessage, Social.get().getConfig().getMessages().getChannelType());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGroupLeave(SocialGroupLeaveEvent event) {
        setDefaultChannel(event.getPlayer());

        Component leftMessage = Social.get().getTextProcessor().parse(event.getPlayer(), Social.get().getConfig().getMessages().getInfo().getPlayerLeftGroup());

        event.getGroupChatChannel().getMembers().forEach(uuid -> {
            SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);
            if (socialPlayer == null) return;

            Social.get().getTextProcessor().send(socialPlayer, leftMessage, Social.get().getConfig().getMessages().getChannelType());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGroupDisband(SocialGroupDisbandEvent event) {
        event.getGroupChatChannel().getMembers().forEach(uuid -> {
            SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);
            if (socialPlayer == null) return;

            Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getInfo().getGroupDisbanded(), Social.get().getConfig().getMessages().getChannelType());

            setDefaultChannel(socialPlayer);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGroupLeaderChange(SocialGroupLeaderChangeEvent event) {
        Component leaderChangeMessage = Social.get().getTextProcessor().parse(event.getLeader(), Social.get().getConfig().getMessages().getInfo().getGroupLeaderChange());

        event.getGroupChatChannel().getMembers().forEach(uuid -> {
            SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);
            if (socialPlayer == null) return;

            Social.get().getTextProcessor().parseAndSend(socialPlayer, leaderChangeMessage, ChannelType.CHAT);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(event.getPlayer().getUniqueId());
        if (socialPlayer == null)
            return;

        GroupChatChannel groupChatChannel = Social.get().getChatManager().getGroupChannelByPlayer(socialPlayer);
        if (groupChatChannel == null)
            return;

        // Not necessary since ChatListener already takes care of this
        groupChatChannel.removeMember(socialPlayer);

        if (groupChatChannel.getLeaderUuid().equals(socialPlayer.getUuid())) {
            if (groupChatChannel.getMembers().size() < 2) {
                Social.get().getChatManager().unregisterChatChannel(groupChatChannel);
            } else {
                Social.get().getChatManager().setGroupChannelLeader(groupChatChannel, groupChatChannel.getMembers().get(0));
            }
        }
    }

    private void setDefaultChannel(SocialPlayer socialPlayer) {
        ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getSettings().getChat().getDefaultChannel());
        if (defaultChannel == null) return;

        Social.get().getPlayerManager().setMainChannel(socialPlayer, defaultChannel);
    }

}
