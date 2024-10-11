package ovh.mythmc.social.common.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.events.groups.SocialGroupDisbandEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupLeaveEvent;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class GroupsListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGroupLeave(SocialGroupLeaveEvent event) {
        setDefaultChannel(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGroupDisband(SocialGroupDisbandEvent event) {
        event.getGroupChatChannel().getMembers().forEach(uuid -> {
            SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);
            if (socialPlayer == null) return;

            setDefaultChannel(socialPlayer);
        });
    }

    private void setDefaultChannel(SocialPlayer socialPlayer) {
        ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getSettings().getChat().getDefaultChannel());
        if (defaultChannel == null) return;

        Social.get().getPlayerManager().setMainChannel(socialPlayer, defaultChannel);
    }

}
