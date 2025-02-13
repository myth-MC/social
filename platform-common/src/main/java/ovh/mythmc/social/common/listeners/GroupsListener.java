package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import ovh.mythmc.gestalt.key.IdentifierKey;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callbacks.group.SocialGroupCreateCallback;
import ovh.mythmc.social.api.callbacks.group.SocialGroupDisbandCallback;
import ovh.mythmc.social.api.callbacks.group.SocialGroupJoinCallback;
import ovh.mythmc.social.api.callbacks.group.SocialGroupLeaderChangeCallback;
import ovh.mythmc.social.api.callbacks.group.SocialGroupLeaveCallback;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

public final class GroupsListener implements Listener {

    public void registerCallbackHandlers() {
        SocialGroupJoinCallback.INSTANCE.registerHandler("social:groupJoinMessage", (ctx) -> {
            Component joinMessage = Social.get().getTextProcessor().parse(ctx.user(), ctx.user().getMainChannel(), Social.get().getConfig().getMessages().getInfo().getPlayerJoinedGroup());

            ctx.groupChatChannel().getMembers().forEach(user -> {
                if (user.getUuid().equals(ctx.user().getUuid())) return;
    
                Social.get().getTextProcessor().send(user, joinMessage, Social.get().getConfig().getMessages().getChannelType(), ctx.groupChatChannel());
            });
        });

        SocialGroupLeaveCallback.INSTANCE.registerHandler("social:groupLeaveMessage", (ctx) -> {
            setDefaultChannel(ctx.user());

            Component leftMessage = Social.get().getTextProcessor().parse(ctx.user(), ctx.user().getMainChannel(), Social.get().getConfig().getMessages().getInfo().getPlayerLeftGroup());
    
            ctx.groupChatChannel().getMembers().forEach(user -> {
                Social.get().getTextProcessor().send(user, leftMessage, Social.get().getConfig().getMessages().getChannelType(), ctx.groupChatChannel());
            });
        });

        SocialGroupCreateCallback.INSTANCE.registerHandler("social:groupCreateMessage", (ctx) -> {
            var leader = ctx.groupChatChannel().getLeader();
        
            // Switch main channel
            Social.get().getUserManager().setMainChannel(leader, ctx.groupChatChannel());
    
            // Message
            int groupCode = Social.get().getChatManager().getGroupChannelByUser(leader).getCode();
            String createdMessage = String.format(Social.get().getConfig().getMessages().getCommands().getCreatedGroup(), groupCode);
            Social.get().getTextProcessor().parseAndSend(leader, createdMessage, Social.get().getConfig().getMessages().getChannelType());
        });

        SocialGroupDisbandCallback.INSTANCE.registerHandler("social:groupDisbandMessage", (ctx) -> {
            ctx.groupChatChannel().getMembers().forEach(user -> {
                Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), Social.get().getConfig().getMessages().getInfo().getGroupDisbanded(), Social.get().getConfig().getMessages().getChannelType());
    
                setDefaultChannel(user);
            });
        });

        SocialGroupLeaderChangeCallback.INSTANCE.registerHandler("social:groupLeaderChangeMessage", (ctx) -> {
            Component leaderChangeMessage = Social.get().getTextProcessor().parse(ctx.leader(), ctx.leader().getMainChannel(), Social.get().getConfig().getMessages().getInfo().getGroupLeaderChange());

            ctx.groupChatChannel().getMembers().forEach(user -> {
                Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), leaderChangeMessage, ChannelType.CHAT);
            });
        });
    }

    public void unregisterCallbackHandlers() {
        SocialGroupJoinCallback.INSTANCE.unregisterHandlers(IdentifierKey.of("social", "groupJoinMessage"));
        SocialGroupLeaveCallback.INSTANCE.unregisterHandlers(IdentifierKey.of("social", "groupLeaveMessage"));
        SocialGroupCreateCallback.INSTANCE.unregisterHandlers(IdentifierKey.of("social", "groupCreateMessage"));
        SocialGroupDisbandCallback.INSTANCE.unregisterHandlers(IdentifierKey.of("social", "groupDisbandMessage"));
        SocialGroupLeaderChangeCallback.INSTANCE.unregisterHandlers(IdentifierKey.of("social", "groupLeaderChangeMessage"));
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
