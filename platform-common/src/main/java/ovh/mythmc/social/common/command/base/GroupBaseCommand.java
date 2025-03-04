package ovh.mythmc.social.common.command.base;

import org.bukkit.permissions.PermissionDefault;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Flag;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Requirement;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

@Command("group")
@SuppressWarnings("null")
public final class GroupBaseCommand {

    @Command("alias")
    @Permission(value = "social.use.group.alias", def = PermissionDefault.TRUE)
    @Requirement(value = "group", messageKey = "not-in-group")
    @Requirement(value = "group-leader", messageKey = "not-group-leader")
    public void alias(SocialUser user, String alias) {
        if (alias.length() > 16) {
            Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getGroupAliasTooLong(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Social.get().getChatManager().setGroupChannelAlias(user.group().get(), alias);
        Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getCommands().getGroupAliasChanged(), Social.get().getConfig().getMessages().getChannelType());
    }

    @Command("chat")
    @Permission(value = "social.use.group.chat", def = PermissionDefault.TRUE)
    @Requirement(value = "group", messageKey = "not-in-group")
    public void chat(SocialUser user) {
        Social.get().getUserManager().setMainChannel(user, user.group().get());
    }

    @Command("code")
    @Permission(value = "social.use.group.code", def = PermissionDefault.TRUE)
    @Requirement(value = "group", messageKey = "not-in-group")
    @Requirement(value = "group-leader", messageKey = "not-group-leader")
    public void code(SocialUser user) {
        Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getCommands().getGroupCode(), Social.get().getConfig().getMessages().getChannelType());
    }

    @Command("create")
    @Permission(value = "social.use.group.create", def = PermissionDefault.TRUE)
    @Requirement(value = "group", messageKey = "already-in-group", invert = true)
    public void create(SocialUser user, @Optional String alias) {
        if (alias == null) {
            Social.get().getChatManager().registerGroupChatChannel(user.getUuid());
        } else {
            if (alias.length() > 16) {
                Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getGroupAliasTooLong(), Social.get().getConfig().getMessages().getChannelType());
                return;
            }

            Social.get().getChatManager().registerGroupChatChannel(user.getUuid(), alias);
        }
    }

    @Command("disband")
    @Permission(value = "social.use.group.disband", def = PermissionDefault.TRUE)
    @Flag(flag = "c", longFlag = "confirm")
    @Requirement(value = "group", messageKey = "not-in-group")
    @Requirement(value = "group-leader", messageKey = "not-group-leader")
    public void disband(SocialUser user, Flags flags) {
        if (flags.hasFlag("c")) {
            Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getCommands().getGroupDisbanded(), Social.get().getConfig().getMessages().getChannelType());
            Social.get().getChatManager().unregisterChatChannel(user.group().get());
            return;
        }

        Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getCommands().getConfirmDisbandAction(), Social.get().getConfig().getMessages().getChannelType());
    }

    @Command("join")
    @Permission(value = "social.use.group.join", def = PermissionDefault.TRUE)
    @Requirement(value = "group", messageKey = "already-in-group", invert = true)
    public void join(SocialUser user, Integer code) {
        GroupChatChannel chatChannel = Social.get().getChatManager().getGroupChannelByCode(code);
        if (chatChannel == null) {
            Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getGroupDoesNotExist(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (chatChannel.addMember(user)) {
            Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getCommands().getJoinedGroup(), Social.get().getConfig().getMessages().getChannelType());
            Social.get().getUserManager().setMainChannel(user, chatChannel);
        } else {
            Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getGroupIsFull(), Social.get().getConfig().getMessages().getChannelType());
        }
    }

    @Command("kick")
    @Permission(value = "social.use.group.kick", def = PermissionDefault.TRUE)
    @Requirement(value = "group", messageKey = "not-in-group")
    @Requirement(value = "group-leader", messageKey = "not-group-leader")
    public void kick(SocialUser user, @Suggestion("group-members") SocialUser target) {
        if (user.equals(target)) {
            Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getChooseAnotherPlayer(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        user.group().get().removeMember(target);
    }

    @Command("leader")
    @Permission(value = "social.use.group.leader", def = PermissionDefault.TRUE)
    @Requirement(value = "group", messageKey = "not-in-group")
    @Requirement(value = "group-leader", messageKey = "not-group-leader")
    public void leader(SocialUser user, @Suggestion("group-members") SocialUser target) {
        if (user.equals(target)) {
            Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getChooseAnotherPlayer(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Social.get().getChatManager().setGroupChannelLeader(user.group().get(), target.getUuid());
    }

    @Command("leave")
    @Permission(value = "social.use.group.leave", def = PermissionDefault.TRUE)
    @Requirement(value = "group", messageKey = "not-in-group")
    public void leave(SocialUser user) {
        final var group = user.group().get();

        if (group.getLeaderUuid().equals(user.getUuid())) {
            if (group.getMembers().size() < 2) {
                Social.get().getChatManager().unregisterChatChannel(group);
                Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getCommands().getGroupDisbanded(), Social.get().getConfig().getMessages().getChannelType());
                return;
            }

            Social.get().getChatManager().setGroupChannelLeader(group, group.getMemberUuids().get(1));
        }

        Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getCommands().getLeftGroup(), Social.get().getConfig().getMessages().getChannelType());
        group.removeMember(user);
    }
    
}
