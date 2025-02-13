package ovh.mythmc.social.common.commands.base;

import org.bukkit.permissions.PermissionDefault;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.api.reactions.ReactionFactory;
import ovh.mythmc.social.api.users.SocialUser;

@Command(value = "reaction", alias = {"e", "emote"})
@Permission(value = "social.use.reaction", def = PermissionDefault.TRUE)
public final class ReactionBaseCommand {

    @Command
    public void execute(SocialUser user, @Suggestion("reaction-categories") String category, @Suggestion("reactions") String identifier) {
        Reaction reaction = Social.get().getReactionManager().get(category, identifier);
        if (reaction == null) {
            Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getUnknownReaction(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        ReactionFactory.get().scheduleReaction(user, reaction);
    }
    
}
