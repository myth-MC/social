package ovh.mythmc.social.common.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.Gestalt;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.events.reactions.SocialReactionCallEvent;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.common.features.ReactionsFeature;

import java.util.*;

public abstract class ReactionCommand {

    public void run(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (!Gestalt.get().isEnabled(ReactionsFeature.class)) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getFeatureNotAvailable(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        SocialPlayer socialPlayer = null;
        if (commandSender instanceof Player player)
            socialPlayer = Social.get().getPlayerManager().get(player.getUniqueId());

        if (socialPlayer == null) {
            String message = Social.get().getConfig().getMessages().getErrors().getCannotBeRunFromConsole();
            SocialAdventureProvider.get().sender(commandSender).sendMessage(MiniMessage.miniMessage().deserialize(message));
            return;
        }

        if (!Social.get().getConfig().getSettings().getCommands().getReaction().enabled()) {
            Social.get().getTextProcessor().parseAndSend(socialPlayer, socialPlayer.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getFeatureNotAvailable(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (args.length < 2) {
            Social.get().getTextProcessor().parseAndSend(socialPlayer, socialPlayer.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getNotEnoughArguments(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        String categoryName = args[0];
        String reactionName = args[1];

        Reaction reaction = Social.get().getReactionManager().get(categoryName, reactionName);
        if (reaction == null) {
            Social.get().getTextProcessor().parseAndSend(socialPlayer, socialPlayer.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getUnknownReaction(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        SocialReactionCallEvent socialReactionCallEvent = new SocialReactionCallEvent(socialPlayer, reaction);
        Bukkit.getPluginManager().callEvent(socialReactionCallEvent);
    }

    public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (args.length == 1) {
            return List.copyOf(Social.get().getReactionManager().getCategories());
        } else if (args.length == 2) {
            List<String> reactions = new ArrayList<>();
            Social.get().getReactionManager().getByCategory(args[0]).forEach(reaction -> reactions.add(reaction.name().toUpperCase()));
            return reactions;
        }

        return List.of();
    }

}
