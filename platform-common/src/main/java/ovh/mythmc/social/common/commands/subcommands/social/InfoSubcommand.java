package ovh.mythmc.social.common.commands.subcommands.social;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.commands.SubCommand;
import ovh.mythmc.social.common.context.SocialMenuContext;
import ovh.mythmc.social.common.gui.impl.PlayerInfoMenu;

public final class InfoSubcommand implements SubCommand {

    @Override
    public void accept(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission("social.command.info")) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (!(commandSender instanceof Player)) {
            String message = Social.get().getConfig().getMessages().getErrors().getCannotBeRunFromConsole();
            SocialAdventureProvider.get().sender(commandSender).sendMessage(MiniMessage.miniMessage().deserialize(message));
            return;
        }

        SocialUser sender = Social.get().getPlayerManager().get(((Player) commandSender).getUniqueId());
        if (sender == null)
            return;

        // Global history
        if (args.length == 0) {
            SocialMenuContext context = SocialMenuContext.builder()
                .viewer(sender)
                .target(sender)
                .build();

            PlayerInfoMenu playerInfo = new PlayerInfoMenu();
            playerInfo.open(context);
            return;
        }

        // Player history
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getPlayerNotFound(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        SocialMenuContext context = SocialMenuContext.builder()
            .viewer(sender)
            .target(Social.get().getPlayerManager().get(target.getUniqueId()))
            .build();

        PlayerInfoMenu playerInfo = new PlayerInfoMenu();
        playerInfo.open(context);
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player))
            return List.of();

        SocialUser socialPlayer = Social.get().getPlayerManager().get(((Player) commandSender).getUniqueId());
        if (socialPlayer == null)
            return List.of();

        if (args.length == 1)
            return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).toList();

        return List.of();
    }

}
