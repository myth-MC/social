package ovh.mythmc.social.common.commands.subcommands.social;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.SubCommand;
import ovh.mythmc.social.common.context.SocialHistoryMenuContext;
import ovh.mythmc.social.common.gui.impl.PlayerHistoryMenu;
import ovh.mythmc.social.common.gui.impl.ChannelHistoryMenu;

public final class HistorySubcommand implements SubCommand {

    @Override
    public void accept(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            String message = Social.get().getConfig().getMessages().getErrors().getCannotBeRunFromConsole();
            SocialAdventureProvider.get().sender(commandSender).sendMessage(MiniMessage.miniMessage().deserialize(message));
            return;
        }

        SocialPlayer sender = Social.get().getPlayerManager().get(((Player) commandSender).getUniqueId());
        if (sender == null)
            return;

        if (args.length < 1) {
            SocialHistoryMenuContext context = SocialHistoryMenuContext.builder()
                .viewer(sender)
                .build();

            ChannelHistoryMenu history = new ChannelHistoryMenu();
            history.open(context);
            return;
        }

        if (args[0].startsWith("?")) {
            ChatChannel chatChannel = Social.get().getChatManager().getChannel(args[0].substring(1));
            if (chatChannel == null) {
                Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getChannelDoesNotExist(), Social.get().getConfig().getMessages().getChannelType());
                return;
            }

            SocialHistoryMenuContext context = SocialHistoryMenuContext.builder()
                .viewer(sender)
                .channel(chatChannel)
                .build();

            ChannelHistoryMenu history = new ChannelHistoryMenu();
            history.open(context);
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getPlayerNotFound(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        SocialHistoryMenuContext context = SocialHistoryMenuContext.builder()
            .viewer(sender)
            .target(Social.get().getPlayerManager().get(target.getUniqueId()))
            .build();

        PlayerHistoryMenu history = new PlayerHistoryMenu();
        history.open(context);
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player))
            return List.of();

        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(((Player) commandSender).getUniqueId());
        if (socialPlayer == null)
            return List.of();

        if (args.length == 1) {
            List<String> scope = new ArrayList<>();

            Social.get().getChatManager().getChannels().stream()
                .filter(channel -> Social.get().getChatManager().hasPermission(socialPlayer, channel))
                .map(channel -> "?" + channel.getName())
                .forEach(scope::add);

            Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).forEach(scope::add);
            return scope;
        }

        return List.of();
    }
    
}
