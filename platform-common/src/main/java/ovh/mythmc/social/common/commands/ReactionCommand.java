package ovh.mythmc.social.common.commands;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.events.reactions.SocialReactionCallEvent;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.reactions.Reaction;

import java.util.*;

public abstract class ReactionCommand {

    public void run(@NotNull SocialPlayer socialPlayer, @NotNull String[] args) {
        if (!Social.get().getConfig().getSettings().getCommands().getReaction().enabled()) {
            Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getErrors().getFeatureNotAvailable(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (args.length < 2) {
            Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getErrors().getNotEnoughArguments(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        String categoryName = args[0];
        String reactionName = args[1];

        Reaction reaction = Social.get().getReactionManager().get(categoryName, reactionName);
        if (reaction == null) {
            Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getErrors().getUnknownReaction(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        SocialReactionCallEvent socialReactionCallEvent = new SocialReactionCallEvent(socialPlayer, reaction);
        Bukkit.getPluginManager().callEvent(socialReactionCallEvent);
    }

    public @NotNull List<String> tabComplete(@NotNull SocialPlayer socialPlayer, @NotNull String[] args) {
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
