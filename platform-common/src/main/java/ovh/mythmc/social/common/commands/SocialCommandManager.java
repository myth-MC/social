package ovh.mythmc.social.common.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import dev.triumphteam.cmd.core.requirement.RequirementKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import lombok.NonNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.emojis.Emoji;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.api.text.parsers.SocialContextualKeyword;
import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.commands.base.GroupBaseCommand;
import ovh.mythmc.social.common.commands.base.PMBaseCommand;
import ovh.mythmc.social.common.commands.base.ReactionBaseCommand;
import ovh.mythmc.social.common.commands.base.SocialBaseCommand;

public final class SocialCommandManager {

    private static SocialCommandManager instance;

    public static void set(@NonNull SocialCommandManager s) {
        if (instance == null)
            instance = s;
    }

    public static SocialCommandManager get() { return instance; }

    private final BukkitCommandManager<SocialUser> manager;

    private final List<Object> registeredCommands = new ArrayList<>();

    public SocialCommandManager(@NonNull Plugin plugin) {
        manager = BukkitCommandManager.create(
            plugin,
            new SocialSenderExtension(),
            builder -> { 
                builder.suggestLowercaseEnum();
            }
        );
    }

    public void registerArguments() {
        manager.registerArgument(SocialUser.class, (sender, arg) -> {
            Player player = Bukkit.getPlayer(arg);
            if (player == null)
                return null;

            return Social.get().getUserManager().getByUuid(player.getUniqueId());
        });
        
        manager.registerArgument(ChatChannel.class, (sender, arg) -> Social.get().getChatManager().getChannel(arg));
    }

    public void registerSuggestions() {
        manager.registerSuggestion(SocialUser.class, (sender, arg) -> Bukkit.getOnlinePlayers().stream()
            .map(Player::getName)
            .collect(Collectors.toList())
        );

        manager.registerSuggestion(ChatChannel.class, (sender, arg) -> Social.get().getChatManager().getChannels().stream()
            .filter(channel -> !(channel instanceof GroupChatChannel) && Social.get().getChatManager().hasPermission(sender, channel))
            .map(ChatChannel::getName)
            .collect(Collectors.toList())
        );

        manager.registerSuggestion(boolean.class, (sender, arg) -> List.of("true", "false"));
        
        manager.registerSuggestion(SuggestionKey.of("announcements"), (sender, arg) -> {
            List<String> integers = new ArrayList<>();
            for (int i = 0; i < Social.get().getAnnouncementManager().getAnnouncements().size(); i++) {
                integers.add(String.valueOf(i));
            }

            return integers;
        });

        manager.registerSuggestion(SuggestionKey.of("placeholders"), (sender, arg) -> 
            Social.get().getTextProcessor().getContextualParsers().stream()
                .filter(parser -> parser instanceof SocialContextualPlaceholder)
                .map(parser -> ((SocialContextualPlaceholder) parser).identifier())
                .toList()
        );

        manager.registerSuggestion(SuggestionKey.of("formatting-options"), (sender, arg) -> {
            List<String> formattingOptions = new ArrayList<>();
            
            Social.get().getEmojiManager().getEmojis().stream()
                .map(Emoji::name)
                .forEach(emojiName -> formattingOptions.add(":" + emojiName + ":"));

            Social.get().getTextProcessor().getContextualParsers().stream()
                .filter(parser -> parser instanceof SocialContextualKeyword)
                .map(keyword -> ((SocialContextualKeyword) keyword).keyword())
                .forEach(keywordName -> formattingOptions.add("[" + keywordName + "]"));

            return formattingOptions;
        });

        manager.registerSuggestion(SuggestionKey.of("reaction-categories"), (sender, arg) ->
            Social.get().getReactionManager().getCategories().stream()
                .filter(category -> !category.equals("HIDDEN"))
                .toList()
        );

        manager.registerSuggestion(SuggestionKey.of("reactions"), (sender, arg) ->
            Social.get().getReactionManager().getByCategory(arg.get(arg.size() - 2)).stream()
                .map(Reaction::name)
                .toList()
        );

        manager.registerSuggestion(SuggestionKey.of("group-members"), (sender, arg) -> {
            if (!sender.hasGroupChatChannel())
                return null;
            
            return sender.getGroupChatChannel().getMembers().stream()
                .map(user -> user.player().get().getName())
                .toList();
            });
    }

    public void registerRequirements() {
        manager.registerRequirement(RequirementKey.of("group"), (sender, context) ->
            sender.hasGroupChatChannel()
        );

        manager.registerRequirement(RequirementKey.of("group-leader"), (sender, context) ->
            sender.hasGroupChatChannel() && sender.getGroupChatChannel().getLeaderUuid().equals(sender.getUuid())
        );
    }

    public void registerMessages() {
        // Default keys
        manager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) ->
            sender.sendParsableMessage(String.format(
                Social.get().getConfig().getMessages().getErrors().getInvalidArgument(),
                context.getInvalidInput(),
                context.getArgumentType().getSimpleName()
            ))
        );

        manager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) ->
            sender.sendParsableMessage(Social.get().getConfig().getMessages().getErrors().getNotEnoughArguments())
        );

        manager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) ->
            sender.sendParsableMessage(Social.get().getConfig().getMessages().getErrors().getTooManyArguments())
        );

        manager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) ->
            sender.sendParsableMessage(String.format(
                Social.get().getConfig().getMessages().getErrors().getInvalidCommand(),
                context.getInvalidInput()
            ))
        );

        manager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) ->
            sender.sendParsableMessage(Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission())
        );

        // Custom keys
        manager.registerMessage(MessageKey.of("not-in-group", MessageContext.class), (sender, context) ->
            sender.sendParsableMessage(Social.get().getConfig().getMessages().getErrors().getDoesNotBelongToAGroup())
        );

        manager.registerMessage(MessageKey.of("already-in-group", MessageContext.class), (sender, context) ->
            sender.sendParsableMessage(Social.get().getConfig().getMessages().getErrors().getAlreadyBelongsToAGroup())
        );

        manager.registerMessage(MessageKey.of("not-group-leader", MessageContext.class), (sender, context) ->
            sender.sendParsableMessage(Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission())
        );
    }

    public void registerCommands() {
        // /social
        registeredCommands.add(new SocialBaseCommand());

        // /group
        if (Social.get().getConfig().getChat().getGroups().isEnabled())
            registeredCommands.add(new GroupBaseCommand());

        // /pm
        if (Social.get().getConfig().getCommands().getPrivateMessage().enabled())
            registeredCommands.add(new PMBaseCommand());

        // /reaction
        if (Social.get().getConfig().getCommands().getReaction().enabled() && Social.get().getConfig().getReactions().isEnabled())
            registeredCommands.add(new ReactionBaseCommand());

        registeredCommands.forEach(manager::registerCommand);
    }

    public void unregisterCommands() {
        registeredCommands.forEach(manager::unregisterCommand);
        registeredCommands.clear();
    }
    
}
