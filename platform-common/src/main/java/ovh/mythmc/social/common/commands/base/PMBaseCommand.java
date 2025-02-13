package ovh.mythmc.social.common.commands.base;

import org.bukkit.permissions.PermissionDefault;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Join;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import ovh.mythmc.social.api.callbacks.message.SocialPrivateMessageSend;
import ovh.mythmc.social.api.callbacks.message.SocialPrivateMessageSendCallback;
import ovh.mythmc.social.api.users.SocialUser;

@Command(value = "pm", alias = {"msg", "w", "whisper", "message"})
@Permission(value = "social.use.pm", def = PermissionDefault.TRUE)
public final class PMBaseCommand {

    @Command
    public void execute(SocialUser sender, SocialUser recipient, @Suggestion("formatting-options") @Join(" ") String plainMessage) {
        var callback = new SocialPrivateMessageSend(recipient, recipient, plainMessage);
        SocialPrivateMessageSendCallback.INSTANCE.handle(callback);
    }

}
