package ovh.mythmc.social.common.command.commands;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.permission.PermissionResult;
import org.incendo.cloud.permission.PredicatePermission;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.common.command.MainCommand;
import ovh.mythmc.social.common.command.parser.UserParser;

public class GroupCommand implements MainCommand<AbstractSocialUser> {

    public static final class Requirements {

        public final static PredicatePermission<AbstractSocialUser> HAS_GROUP = user -> {
            if (user.group().isPresent())
                return PermissionResult.allowed(Permission.EMPTY);

            return PermissionResult.denied(Permission.EMPTY);
        };

        public final static PredicatePermission<AbstractSocialUser> NOT_IN_GROUP = user -> {
            if (user.group().isEmpty())
                return PermissionResult.allowed(Permission.EMPTY);

            return PermissionResult.denied(Permission.EMPTY);
        };

        public final static PredicatePermission<AbstractSocialUser> IS_GROUP_LEADER = user -> {
            if (user.group().isPresent() && user.group().get().getLeaderUuid().equals(user.uuid()))
                return PermissionResult.allowed(Permission.EMPTY);

            return PermissionResult.denied(Permission.EMPTY);
        };

    }

    @Override
    public boolean canRegister() {
        return Social.get().getConfig().getChat().getGroups().isEnabled();
    }

    @Override
    public void register(@NotNull CommandManager<AbstractSocialUser> commandManager) {
        final Command.Builder<AbstractSocialUser> groupCommand = commandManager.commandBuilder("group");


        // /group alias
        commandManager.command(groupCommand
            .literal("alias")
            .commandDescription(Description.of("Sets the description of your group"))
            .permission(Permission.allOf(Permission.of("social.use.group.alias"), Requirements.HAS_GROUP, Requirements.IS_GROUP_LEADER))
            .required("alias", StringParser.stringParser())
            .handler(ctx -> {
                final String alias = ctx.get("alias");
                if (alias.length() > 16) {
                    Social.get().getTextProcessor().parseAndSend(ctx.sender(), Social.get().getConfig().getMessages().getErrors().getGroupAliasTooLong(), Social.get().getConfig().getMessages().getChannelType());
                    return;
                }

                Social.get().getChatManager().setChannelAlias(ctx.sender().group().get(), alias);
                Social.get().getTextProcessor().parseAndSend(ctx.sender(), Social.get().getConfig().getMessages().getCommands().getGroupAliasChanged(), Social.get().getConfig().getMessages().getChannelType());
            })
        );

        // /group chat
        commandManager.command(groupCommand
            .literal("chat")
            .commandDescription(Description.of("Switches to your group's chat channel"))
            .permission(Permission.allOf(Permission.of("social.use.group.chat"), Requirements.HAS_GROUP))
            .handler(ctx -> {
                Social.get().getUserManager().setMainChannel(ctx.sender(), ctx.sender().group().get(), true);
            })
        );

        // /group code
        commandManager.command(groupCommand
            .literal("code")
            .commandDescription(Description.of("Displays your group's invite code"))
            .permission(Permission.allOf(Permission.of("social.use.group.code"), Requirements.HAS_GROUP))
            .handler(ctx -> {
                Social.get().getTextProcessor().parseAndSend(ctx.sender(), Social.get().getConfig().getMessages().getCommands().getGroupCode(), Social.get().getConfig().getMessages().getChannelType());
            })
        );

        // /group create
        commandManager.command(groupCommand
            .literal("create")
            .commandDescription(Description.of("Creates a new group"))
            .permission(Permission.allOf(Permission.of("social.use.group.create"), Requirements.NOT_IN_GROUP))
            .optional("alias", StringParser.stringParser())
            .handler(ctx -> {
                final String alias = ctx.getOrDefault("alias", null);
                if (alias == null) {
                    Social.get().getChatManager().registerGroupChatChannel(ctx.sender().uuid());
                } else {
                    if (alias.length() > 16) {
                        Social.get().getTextProcessor().parseAndSend(ctx.sender(), Social.get().getConfig().getMessages().getErrors().getGroupAliasTooLong(), Social.get().getConfig().getMessages().getChannelType());
                        return;
                    }
        
                    Social.get().getChatManager().registerGroupChatChannel(ctx.sender().uuid(), alias);
                }
            })
        );

        // /group disband
        commandManager.command(groupCommand
            .literal("disband")
            .commandDescription(Description.of("Disbands your group"))
            .permission(Permission.allOf(Permission.of("social.use.group.disband"), Requirements.HAS_GROUP, Requirements.IS_GROUP_LEADER))
            .flag(commandManager.flagBuilder("confirm"))
            .handler(ctx -> {
                if (ctx.flags().hasFlag("c")) {
                    Social.get().getTextProcessor().parseAndSend(ctx.sender(), Social.get().getConfig().getMessages().getCommands().getGroupDisbanded(), Social.get().getConfig().getMessages().getChannelType());
                    Social.get().getChatManager().unregisterChatChannel(ctx.sender().group().get());
                    return;
                }
        
                Social.get().getTextProcessor().parseAndSend(ctx.sender(), Social.get().getConfig().getMessages().getCommands().getConfirmDisbandAction(), Social.get().getConfig().getMessages().getChannelType());
            })
        );

        // /group join
        commandManager.command(groupCommand
            .literal("join")
            .commandDescription(Description.of("Joins a group by its code"))
            .permission(Permission.allOf(Permission.of("social.use.group.join"), Requirements.NOT_IN_GROUP))
            .required("code", IntegerParser.integerParser(0, 999999))
            .handler(ctx -> {
                final Integer code = ctx.get("code");

                final GroupChatChannel chatChannel = Social.get().getChatManager().getGroupChannelByCode(code);
                if (chatChannel == null) {
                    Social.get().getTextProcessor().parseAndSend(ctx.sender(), Social.get().getConfig().getMessages().getErrors().getGroupDoesNotExist(), Social.get().getConfig().getMessages().getChannelType());
                    return;
                }

                if (chatChannel.addMember(ctx.sender())) {
                    Social.get().getTextProcessor().parseAndSend(ctx.sender(), Social.get().getConfig().getMessages().getCommands().getJoinedGroup(), Social.get().getConfig().getMessages().getChannelType());
                    Social.get().getUserManager().setMainChannel(ctx.sender(), chatChannel, true);
                } else {
                    Social.get().getTextProcessor().parseAndSend(ctx.sender(), Social.get().getConfig().getMessages().getErrors().getGroupIsFull(), Social.get().getConfig().getMessages().getChannelType());
                }
            })
        );

        // /group kick
        commandManager.command(groupCommand
            .literal("kick")
            .commandDescription(Description.of("Kicks a member from your group"))
            .permission(Permission.allOf(Permission.of("social.use.group.kick"), Requirements.HAS_GROUP, Requirements.IS_GROUP_LEADER))
            .required("user", UserParser.userParser()) // Todo: suggestions
            .handler(ctx -> {
                final AbstractSocialUser target = ctx.get("user");
                if (ctx.sender().equals(target)) {
                    Social.get().getTextProcessor().parseAndSend(ctx.sender(), Social.get().getConfig().getMessages().getErrors().getChooseAnotherPlayer(), Social.get().getConfig().getMessages().getChannelType());
                    return;
                }

                if (target.group().isEmpty() || target.group().get().equals(ctx.sender().group().get())) {
                    // user isn't a member of your group
                    return;
                }
        
                ctx.sender().group().get().removeMember(target);
            })
        );

        // /group leader
        commandManager.command(groupCommand
            .literal("leader")
            .commandDescription(Description.of("Passes the leadership of your group to another member"))
            .permission(Permission.allOf(Permission.of("social.use.group.leader"), Requirements.HAS_GROUP, Requirements.IS_GROUP_LEADER))
            .required("user", UserParser.userParser()) // Todo: suggestions
            .handler(ctx -> {
                final AbstractSocialUser target = ctx.get("user");
                if (ctx.sender().equals(target)) {
                    Social.get().getTextProcessor().parseAndSend(ctx.sender(), Social.get().getConfig().getMessages().getErrors().getChooseAnotherPlayer(), Social.get().getConfig().getMessages().getChannelType());
                    return;
                }

                if (target.group().isEmpty() || target.group().get().equals(ctx.sender().group().get())) {
                    // user isn't a member of your group
                    return;
                }
        
                Social.get().getChatManager().setGroupChannelLeader(ctx.sender().group().get(), target.uuid());
            })
        );

        // /group leave
        commandManager.command(groupCommand
            .literal("leave")
            .commandDescription(Description.of("Leaves your group"))
            .permission(Permission.allOf(Permission.of("social.use.group.leave"), Requirements.HAS_GROUP))
            .handler(ctx -> {
                final var group = ctx.sender().group().get();

                if (group.getLeaderUuid().equals(ctx.sender().uuid())) {
                    if (group.members().size() < 2) {
                        Social.get().getChatManager().unregisterChatChannel(group);
                        Social.get().getTextProcessor().parseAndSend(ctx.sender(), Social.get().getConfig().getMessages().getCommands().getGroupDisbanded(), Social.get().getConfig().getMessages().getChannelType());
                        return;
                    }
        
                    Social.get().getChatManager().setGroupChannelLeader(group, group.members().stream().findFirst().get().uuid());
                }
        
                Social.get().getTextProcessor().parseAndSend(ctx.sender(), Social.get().getConfig().getMessages().getCommands().getLeftGroup(), Social.get().getConfig().getMessages().getChannelType());
                group.removeMember(ctx.sender());
            })
        );
    }
    
}
