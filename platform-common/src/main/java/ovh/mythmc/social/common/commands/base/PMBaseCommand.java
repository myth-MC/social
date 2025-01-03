package ovh.mythmc.social.common.commands.base;

import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionDefault;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Join;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import ovh.mythmc.social.api.events.chat.SocialPrivateMessageEvent;
import ovh.mythmc.social.api.users.SocialUser;

@Command(value = "pm", alias = {"msg", "w", "whisper", "message"})
@Permission(value = "social.use.pm", def = PermissionDefault.TRUE)
public final class PMBaseCommand {

    @Command
    public void execute(SocialUser sender, SocialUser recipient, @Suggestion("formatting-options") @Join(" ") String message) {
        SocialPrivateMessageEvent socialPrivateMessageEvent = new SocialPrivateMessageEvent(sender, recipient, message);
        Bukkit.getPluginManager().callEvent(socialPrivateMessageEvent);
    }

}
