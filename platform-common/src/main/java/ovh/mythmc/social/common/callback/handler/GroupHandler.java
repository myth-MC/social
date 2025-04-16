package ovh.mythmc.social.common.callback.handler;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.group.SocialGroupCreateCallback;
import ovh.mythmc.social.api.callback.group.SocialGroupDisbandCallback;
import ovh.mythmc.social.api.callback.group.SocialGroupJoinCallback;
import ovh.mythmc.social.api.callback.group.SocialGroupLeaderChangeCallback;
import ovh.mythmc.social.api.callback.group.SocialGroupLeaveCallback;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.common.callback.game.UserPresence;
import ovh.mythmc.social.common.callback.game.UserPresenceCallback;

public final class GroupHandler implements SocialCallbackHandler {

    private static class IdentifierKeys {

        static final String GROUP_JOIN_MESSAGE = "social:group-join-message";
        static final String GROUP_LEAVE_MESSAGE = "social:group-leave-message";
        static final String GROUP_CREATE_MESSAGE = "social:group-create-message";
        static final String GROUP_DISBAND_MESSAGE = "social:group-disband-message";
        static final String GROUP_LEADER_CHANGE_MESSAGE = "social:group-leader-change-message";

    }

    @Override
    public void register() {
        SocialGroupJoinCallback.INSTANCE.registerHandler(IdentifierKeys.GROUP_JOIN_MESSAGE, (ctx) -> {
            Component joinMessage = Social.get().getTextProcessor().parse(ctx.user(), ctx.user().mainChannel(), Social.get().getConfig().getMessages().getInfo().getPlayerJoinedGroup());

            ctx.groupChatChannel().members().forEach(user -> {
                if (user.uuid().equals(ctx.user().uuid())) return;
    
                Social.get().getTextProcessor().send(user, joinMessage, Social.get().getConfig().getMessages().getChannelType(), ctx.groupChatChannel());
            });
        });

        SocialGroupLeaveCallback.INSTANCE.registerHandler(IdentifierKeys.GROUP_LEAVE_MESSAGE, (ctx) -> {
            setDefaultChannel(ctx.user());

            Component leftMessage = Social.get().getTextProcessor().parse(ctx.user(), ctx.user().mainChannel(), Social.get().getConfig().getMessages().getInfo().getPlayerLeftGroup());
    
            ctx.groupChatChannel().members().forEach(user -> {
                Social.get().getTextProcessor().send(user, leftMessage, Social.get().getConfig().getMessages().getChannelType(), ctx.groupChatChannel());
            });
        });

        SocialGroupCreateCallback.INSTANCE.registerHandler(IdentifierKeys.GROUP_CREATE_MESSAGE, (ctx) -> {
            var leader = ctx.groupChatChannel().leader();
        
            // Switch main channel
            Social.get().getUserManager().setMainChannel(leader, ctx.groupChatChannel(), true);
    
            // Message
            int groupCode = ctx.groupChatChannel().code();
            String createdMessage = String.format(Social.get().getConfig().getMessages().getCommands().getCreatedGroup(), groupCode);
            Social.get().getTextProcessor().parseAndSend(leader, createdMessage, Social.get().getConfig().getMessages().getChannelType());
        });

        SocialGroupDisbandCallback.INSTANCE.registerHandler(IdentifierKeys.GROUP_DISBAND_MESSAGE, (ctx) -> {
            ctx.groupChatChannel().members().forEach(user -> {
                Social.get().getTextProcessor().parseAndSend(user, user.mainChannel(), Social.get().getConfig().getMessages().getInfo().getGroupDisbanded(), Social.get().getConfig().getMessages().getChannelType());
    
                setDefaultChannel(user);
            });
        });

        SocialGroupLeaderChangeCallback.INSTANCE.registerHandler(IdentifierKeys.GROUP_LEADER_CHANGE_MESSAGE, (ctx) -> {
            Component leaderChangeMessage = Social.get().getTextProcessor().parse(ctx.leader(), ctx.leader().mainChannel(), Social.get().getConfig().getMessages().getInfo().getGroupLeaderChange());

            ctx.groupChatChannel().members().forEach(user -> {
                Social.get().getTextProcessor().parseAndSend(user, user.mainChannel(), leaderChangeMessage, ChatChannel.ChannelType.CHAT);
            });
        });

        UserPresenceCallback.INSTANCE.registerHandler("social:group-clear", ctx -> {
            if (!ctx.type().equals(UserPresence.Type.QUIT))
                return;

            // Remove user from group and change its leader if necessary
            ctx.user().ifPresent(user -> {
                user.group().ifPresent(group -> {
                    // Not necessary since ChatListener already takes care of this
                    group.removeMember(user);

                    if (group.leaderUuid().get().equals(user.uuid())) {
                        if (group.members().isEmpty()) {
                            Social.get().getChatManager().unregisterChatChannel(group);
                        } else {
                            group.leader(group.members().stream().findFirst().get());
                        }
                    }
                });
            });
        });
    }

    @Override
    public void unregister() {
        UserPresenceCallback.INSTANCE.unregisterHandlers("social:group-clear");

        SocialGroupJoinCallback.INSTANCE.unregisterHandlers(IdentifierKeys.GROUP_JOIN_MESSAGE);
        SocialGroupLeaveCallback.INSTANCE.unregisterHandlers(IdentifierKeys.GROUP_LEAVE_MESSAGE);
        SocialGroupCreateCallback.INSTANCE.unregisterHandlers(IdentifierKeys.GROUP_CREATE_MESSAGE);
        SocialGroupDisbandCallback.INSTANCE.unregisterHandlers(IdentifierKeys.GROUP_DISBAND_MESSAGE);
        SocialGroupLeaderChangeCallback.INSTANCE.unregisterHandlers(IdentifierKeys.GROUP_LEADER_CHANGE_MESSAGE);
    }

    private static void setDefaultChannel(AbstractSocialUser user) {
        final ChatChannel defaultChannel = Social.get().getChatManager().getCachedOrDefault(user);
        if (defaultChannel == null) return;

        Social.get().getUserManager().setMainChannel(user, defaultChannel, true);
    }
    
}
