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
        Component joinMessage = Social.get().getTextProcessor().parse(event.getPlayer(), event.getPlayer().getMainChannel(), Social.get().getConfig().getMessages().getInfo().getPlayerJoinedGroup());

        event.getGroupChatChannel().getMembers().forEach(uuid -> {
            if (uuid.equals(event.getPlayer().getUuid())) return;

            SocialUser socialPlayer = Social.get().getUserManager().get(uuid);
            if (socialPlayer == null) return;

            Social.get().getTextProcessor().send(socialPlayer, joinMessage, Social.get().getConfig().getMessages().getChannelType());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGroupLeave(SocialGroupLeaveEvent event) {
        setDefaultChannel(event.getPlayer());

        Component leftMessage = Social.get().getTextProcessor().parse(event.getPlayer(), event.getPlayer().getMainChannel(), Social.get().getConfig().getMessages().getInfo().getPlayerLeftGroup());

        event.getGroupChatChannel().getMembers().forEach(uuid -> {
            SocialUser socialPlayer = Social.get().getUserManager().get(uuid);
            if (socialPlayer == null) return;

            Social.get().getTextProcessor().send(socialPlayer, leftMessage, Social.get().getConfig().getMessages().getChannelType());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGroupCreate(SocialGroupCreateEvent event) {
        SocialUser socialPlayer = Social.get().getUserManager().get(event.getGroupChatChannel().getLeaderUuid());
        if (socialPlayer == null) return;

        Social.get().getUserManager().setMainChannel(socialPlayer, event.getGroupChatChannel());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGroupDisband(SocialGroupDisbandEvent event) {
        event.getGroupChatChannel().getMembers().forEach(uuid -> {
            SocialUser socialPlayer = Social.get().getUserManager().get(uuid);
            if (socialPlayer == null) return;

            Social.get().getTextProcessor().parseAndSend(socialPlayer, socialPlayer.getMainChannel(), Social.get().getConfig().getMessages().getInfo().getGroupDisbanded(), Social.get().getConfig().getMessages().getChannelType());

            setDefaultChannel(socialPlayer);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGroupLeaderChange(SocialGroupLeaderChangeEvent event) {
        Component leaderChangeMessage = Social.get().getTextProcessor().parse(event.getLeader(), event.getLeader().getMainChannel(), Social.get().getConfig().getMessages().getInfo().getGroupLeaderChange());

        event.getGroupChatChannel().getMembers().forEach(uuid -> {
            SocialUser socialPlayer = Social.get().getUserManager().get(uuid);
            if (socialPlayer == null) return;

            Social.get().getTextProcessor().parseAndSend(socialPlayer, socialPlayer.getMainChannel(), leaderChangeMessage, ChannelType.CHAT);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        SocialUser socialPlayer = Social.get().getUserManager().get(event.getPlayer().getUniqueId());
        if (socialPlayer == null)
            return;

        GroupChatChannel groupChatChannel = Social.get().getChatManager().getGroupChannelByPlayer(socialPlayer);
        if (groupChatChannel == null)
            return;

        // Not necessary since ChatListener already takes care of this
        groupChatChannel.removeMember(socialPlayer);

        if (groupChatChannel.getLeaderUuid().equals(socialPlayer.getUuid())) {
            if (groupChatChannel.getMembers().size() < 1) {
                Social.get().getChatManager().unregisterChatChannel(groupChatChannel);
            } else {
                Social.get().getChatManager().setGroupChannelLeader(groupChatChannel, groupChatChannel.getMembers().get(0));
            }
        }
    }

    private void setDefaultChannel(SocialUser socialPlayer) {
        ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getSettings().getChat().getDefaultChannel());
        if (defaultChannel == null) return;

        Social.get().getUserManager().setMainChannel(socialPlayer, defaultChannel);
    }

}
