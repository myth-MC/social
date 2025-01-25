package ovh.mythmc.social.common.commands.base;

import org.bukkit.permissions.PermissionDefault;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.users.SocialUser;

@Command("unignore")
@Permission(value = "social.use.unignore", def = PermissionDefault.TRUE)
public final class UnignoreBaseCommand {
    
    @Command
    public void execute(SocialUser sender, SocialUser target) {
        if (!Social.get().getUserManager().isIgnored(sender, target)) {
            Social.get().getTextProcessor().parseAndSend(sender, target.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getUserIsNotIgnored(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Social.get().getUserManager().unignore(sender, target);
        String successMessage = String.format(Social.get().getConfig().getMessages().getCommands().getUserUnignored(), target.getNickname());
        Social.get().getTextProcessor().parseAndSend(sender, sender.getMainChannel(), successMessage, Social.get().getConfig().getMessages().getChannelType());
    }

}
