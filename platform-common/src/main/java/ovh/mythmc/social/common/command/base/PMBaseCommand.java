package ovh.mythmc.social.common.command.base;

import org.bukkit.permissions.PermissionDefault;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Join;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.message.SocialPrivateMessageSend;
import ovh.mythmc.social.api.callback.message.SocialPrivateMessageSendCallback;
import ovh.mythmc.social.api.user.SocialUser;

@Command(value = "pm", alias = {"msg", "w", "whisper", "message"})
@Permission(value = "social.use.pm", def = PermissionDefault.TRUE)
public final class PMBaseCommand {

    @Command
    public void execute(SocialUser sender, SocialUser recipient, @Suggestion("formatting-options") @Join(" ") String plainMessage) {
        var callback = new SocialPrivateMessageSend(recipient, recipient, plainMessage);
        SocialPrivateMessageSendCallback.INSTANCE.invoke(callback, result -> {
            if (!result.cancelled())
                Social.get().getChatManager().sendPrivateMessage(result.sender(), result.recipient(), result.plainMessage());
        });     
    }

}
