package ovh.mythmc.social.common.commands.base;

import org.bukkit.permissions.PermissionDefault;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.api.users.SocialUser.IgnoreScope;

@Command("ignore")
@Permission(value = "social.use.ignore", def = PermissionDefault.TRUE)
public final class IgnoreBaseCommand {
    
    @Command
    public void execute(SocialUser sender, SocialUser target, @Optional IgnoreScope scope) {
        if (sender.equals(target)) {
            Social.get().getTextProcessor().parseAndSend(sender, sender.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getCannotIgnoreYourself(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (Social.get().getUserManager().isIgnored(sender, target)) {
            Social.get().getTextProcessor().parseAndSend(sender, sender.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getUserIsAlreadyIgnored(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Social.get().getUserManager().ignore(sender, target, java.util.Optional.ofNullable(scope).orElse(IgnoreScope.ALL));
        String successMessage = String.format(Social.get().getConfig().getMessages().getCommands().getUserIgnored(), target.getNickname());
        Social.get().getTextProcessor().parseAndSend(sender, sender.getMainChannel(), successMessage, Social.get().getConfig().getMessages().getChannelType());
    }

}
