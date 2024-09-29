package ovh.mythmc.social.common.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
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

    public void run(@NotNull Audience sender, @NotNull String[] args) {
        Optional<UUID> uuid = sender.get(Identity.UUID);
        if (uuid.isEmpty()) {
            // error: command cannot be run from console
            return;
        }

        SocialPlayer player = Social.get().getPlayerManager().get(uuid.get());

        if (!Social.get().getConfig().getSettings().getCommands().getReaction().enabled()) {
            processor.parseAndSend(player, messages.getErrors().getFeatureNotAvailable(), messages.getChannelType());
            return;
        }

        if (args.length < 2) {
            processor.parseAndSend(player, messages.getErrors().getNotEnoughArguments(), messages.getChannelType());
            return;
        }

        String categoryName = args[0];
        String reactionName = args[1];

        Reaction reaction = Social.get().getReactionManager().get(categoryName, reactionName);
        if (reaction == null) {
            processor.parseAndSend(player, messages.getErrors().getUnknownReaction(), messages.getChannelType());
            return;
        }

        SocialReactionCallEvent socialReactionCallEvent = new SocialReactionCallEvent(player, reaction);
        Bukkit.getPluginManager().callEvent(socialReactionCallEvent);
    }

    public @NotNull Collection<String> getSuggestions(@NotNull String[] args) {
        if (args.length == 1) {
            return Social.get().getReactionManager().getCategories();
        } else if (args.length == 2) {
            List<String> reactions = new ArrayList<>();
            Social.get().getReactionManager().getByCategory(args[0]).forEach(reaction -> reactions.add(reaction.name().toUpperCase()));
            return reactions;
        }

        return List.of();
    }

}
