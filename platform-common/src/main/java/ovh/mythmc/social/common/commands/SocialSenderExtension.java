package ovh.mythmc.social.common.commands;

import java.util.Collections;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.extention.ValidationResult;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.users.SocialUser;

public final class SocialSenderExtension implements SenderExtension<CommandSender, SocialUser> {

    @Override
    public @NotNull Set<Class<? extends SocialUser>> getAllowedSenders() {
        return Collections.singleton(SocialUser.class);
    }

    @Override
    public @NotNull ValidationResult<@NotNull MessageKey<@NotNull MessageContext>> validate(@NotNull CommandMeta meta,
            @NotNull Class<?> allowedSender, @NotNull SocialUser sender) {
        if (sender == null)// || sender instanceof SocialUser.Dummy)
            return invalid(BukkitMessageKey.PLAYER_ONLY);

        return valid();
    }

    @Override
    public @NotNull SocialUser map(@NotNull CommandSender sender) {
        if (sender instanceof Player player && player != null)
            return Social.get().getUserManager().getByUuid(player.getUniqueId());

        return SocialUser.dummy();
    }

    @Override
    public @NotNull CommandSender mapBackwards(@NotNull SocialUser user) {
        return user.asCommandSender();
    }
    
}