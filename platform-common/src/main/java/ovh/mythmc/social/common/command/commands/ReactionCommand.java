package ovh.mythmc.social.common.command.commands;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.reaction.Reaction;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.common.command.MainCommand;
import ovh.mythmc.social.common.command.parser.ReactionParser;

public final class ReactionCommand implements MainCommand<SocialUser> {

    @Override
    public boolean canRegister() {
        return Social.get().getConfig().getReactions().isEnabled() &&
                Social.get().getConfig().getCommands().getReaction().enabled();
    }

    @Override
    public void register(@NotNull CommandManager<SocialUser> commandManager) {
        final Command.Builder<SocialUser> reactionCommand = commandManager.commandBuilder("reaction", "emote", "e")
                .commandDescription(Description.of("Shows a reaction"))
                .permission("social.use.reaction")
                .required("reaction", ReactionParser.reactionParser())
                .handler(ctx -> {
                    final Reaction reaction = ctx.get("reaction");
                    Social.get().getReactionFactory().play(ctx.sender(), reaction);
                });

        commandManager.command(reactionCommand);
    }

}
