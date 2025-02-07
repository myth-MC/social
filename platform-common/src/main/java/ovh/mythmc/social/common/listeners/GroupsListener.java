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
import ovh.mythmc.social.api.events.groups.*;
import ovh.mythmc.social.api.users.SocialUser;

public final class GroupsListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGroupJoin(SocialGroupJoinEvent event) {
        Component joinMessage = Social.get().getTextProcessor().parse(event.getUser(), event.getUser().getMainChannel(), Social.get().getConfig().getMessages().getInfo().getPlayerJoinedGroup());

        event.getGroupChatChannel().getMembers().forEach(user -> {
            if (user.getUuid().equals(event.getUser().getUuid())) return;

            Social.get().getTextProcessor().send(user, joinMessage, Social.get().getConfig().getMessages().getChannelType(), event.getGroupChatChannel());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGroupLeave(SocialGroupLeaveEvent event) {
        setDefaultChannel(event.getUser());

        Component leftMessage = Social.get().getTextProcessor().parse(event.getUser(), event.getUser().getMainChannel(), Social.get().getConfig().getMessages().getInfo().getPlayerLeftGroup());

        event.getGroupChatChannel().getMembers().forEach(user -> {
            Social.get().getTextProcessor().send(user, leftMessage, Social.get().getConfig().getMessages().getChannelType(), event.getGroupChatChannel());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGroupCreate(SocialGroupCreateEvent event) {
        SocialUser user = Social.get().getUserManager().getByUuid(event.getGroupChatChannel().getLeaderUuid());
        if (user == null) return;

        Social.get().getUserManager().setMainChannel(user, event.getGroupChatChannel());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGroupDisband(SocialGroupDisbandEvent event) {
        event.getGroupChatChannel().getMembers().forEach(user -> {
            Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), Social.get().getConfig().getMessages().getInfo().getGroupDisbanded(), Social.get().getConfig().getMessages().getChannelType());

            setDefaultChannel(user);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGroupLeaderChange(SocialGroupLeaderChangeEvent event) {
        Component leaderChangeMessage = Social.get().getTextProcessor().parse(event.getLeader(), event.getLeader().getMainChannel(), Social.get().getConfig().getMessages().getInfo().getGroupLeaderChange());

        event.getGroupChatChannel().getMembers().forEach(user -> {
            Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), leaderChangeMessage, ChannelType.CHAT);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        SocialUser user = Social.get().getUserManager().getByUuid(event.getPlayer().getUniqueId());
        if (user == null)
            return;

        GroupChatChannel groupChatChannel = Social.get().getChatManager().getGroupChannelByUser(user);
        if (groupChatChannel == null)
            return;

        // Not necessary since ChatListener already takes care of this
        groupChatChannel.removeMember(user);

        if (groupChatChannel.getLeaderUuid().equals(user.getUuid())) {
            if (groupChatChannel.getMembers().size() < 1) {
                Social.get().getChatManager().unregisterChatChannel(groupChatChannel);
            } else {
                Social.get().getChatManager().setGroupChannelLeader(groupChatChannel, groupChatChannel.getMemberUuids().get(0));
            }
        }
    }

    private void setDefaultChannel(SocialUser user) {
        ChatChannel defaultChannel = Social.get().getChatManager().getDefaultChannel();
        if (defaultChannel == null) return;

        Social.get().getUserManager().setMainChannel(user, defaultChannel);
    }

}
