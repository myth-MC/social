package ovh.mythmc.social.common.commands;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.SocialMessages;
import ovh.mythmc.social.api.events.reactions.SocialReactionCallEvent;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.api.text.SocialTextProcessor;

import java.util.*;

public abstract class ReactionCommand {

    private final SocialTextProcessor processor = Social.get().getTextProcessor();
    private final SocialMessages messages = Social.get().getConfig().getMessages();

    public void run(@NotNull SocialPlayer socialPlayer, @NotNull String[] args) {
        if (!Social.get().getConfig().getSettings().getCommands().getReaction().enabled()) {
            processor.parseAndSend(socialPlayer, messages.getErrors().getFeatureNotAvailable(), messages.getChannelType());
            return;
        }

        if (args.length < 2) {
            processor.parseAndSend(socialPlayer, messages.getErrors().getNotEnoughArguments(), messages.getChannelType());
            return;
        }

        String categoryName = args[0];
        String reactionName = args[1];

        Reaction reaction = Social.get().getReactionManager().get(categoryName, reactionName);
        if (reaction == null) {
            processor.parseAndSend(socialPlayer, messages.getErrors().getUnknownReaction(), messages.getChannelType());
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
