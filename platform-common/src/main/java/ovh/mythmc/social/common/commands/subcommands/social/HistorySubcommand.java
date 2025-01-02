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
import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.commands.SubCommand;
import ovh.mythmc.social.common.context.SocialHistoryMenuContext;
import ovh.mythmc.social.common.context.SocialHistoryMenuContext.HeaderType;
import ovh.mythmc.social.common.gui.impl.HistoryMenu;

public final class HistorySubcommand implements SubCommand {

    @Override
    public void accept(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission("social.command.history")) {
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
        if (args.length < 1) {
            SocialHistoryMenuContext context = SocialHistoryMenuContext.builder()
                .messages(Social.get().getChatManager().getHistory().get())
                .viewer(sender)
                .build();

            HistoryMenu history = new HistoryMenu();
            history.open(context);
            return;
        }

        // Channel history
        if (args[0].startsWith("?")) {
            ChatChannel chatChannel = Social.get().getChatManager().getChannel(args[0].substring(1));
            if (chatChannel == null) {
                Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getChannelDoesNotExist(), Social.get().getConfig().getMessages().getChannelType());
                return;
            }

            SocialHistoryMenuContext context = SocialHistoryMenuContext.builder()
                .headerType(HeaderType.CHANNEL)
                .messages(Social.get().getChatManager().getHistory().getByChannel(chatChannel))
                .viewer(sender)
                .channel(chatChannel)
                .build();

            HistoryMenu history = new HistoryMenu();
            history.open(context);
            return;
        }

        // Thread history
        if (args[0].startsWith("#")) {
            SocialMessageContext message = Social.get().getChatManager().getHistory().getById(tryParse(args[0].substring(1)));
            if (message == null) {
                // message does not exist
                return;
            }

            SocialHistoryMenuContext context = SocialHistoryMenuContext.builder()
                .headerType(HeaderType.THREAD)
                .messages(Social.get().getChatManager().getHistory().getThread(message, 128))
                .viewer(sender)
                .build();

            HistoryMenu history = new HistoryMenu();
            history.open(context);
            return;
        }

        // Special options
        /*
        if (args[0].startsWith("!")) {
            switch (args[0].substring(1)) {
                case "clear" -> {
                    if (args.length < 2) {

                    }
                }
            }

            return;
        }
        */

        // Player history
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getPlayerNotFound(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        SocialHistoryMenuContext context = SocialHistoryMenuContext.builder()
            .headerType(HeaderType.PLAYER)
            .messages(Social.get().getChatManager().getHistory().getByPlayer(Social.get().getPlayerManager().get(target.getUniqueId())))
            .viewer(sender)
            .target(Social.get().getPlayerManager().get(target.getUniqueId()))
            .build();

        HistoryMenu history = new HistoryMenu();
        history.open(context);
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player))
            return List.of();

        SocialUser socialPlayer = Social.get().getPlayerManager().get(((Player) commandSender).getUniqueId());
        if (socialPlayer == null)
            return List.of();

        if (args.length == 1) {
            List<String> scope = new ArrayList<>();

            // Channels
            Social.get().getChatManager().getChannels().stream()
                .filter(channel -> Social.get().getChatManager().hasPermission(socialPlayer, channel))
                .map(channel -> "?" + channel.getName())
                .forEach(scope::add);

            // Players
            Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).forEach(scope::add);

            // Special options
            //scope.addAll(List.of("!clear"));
            return scope;
        }

        return List.of();
    }

    private Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
}
