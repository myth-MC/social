package ovh.mythmc.social.common.commands.subcommands.social;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.SubCommand;
import ovh.mythmc.social.common.context.SocialMenuContext;
import ovh.mythmc.social.common.gui.impl.EmojiDictionaryMenu;
import ovh.mythmc.social.common.gui.impl.KeywordDictionaryMenu;

public class DictionarySubcommand implements SubCommand {

    @Override
    public void accept(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission("social.command.dictionary")) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (!(commandSender instanceof Player)) {
            String message = Social.get().getConfig().getMessages().getErrors().getCannotBeRunFromConsole();
            SocialAdventureProvider.get().sender(commandSender).sendMessage(MiniMessage.miniMessage().deserialize(message));
            return;
        }

        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(((Player) commandSender).getUniqueId());
        if (socialPlayer == null)
            return;

        if (args.length == 0) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughArguments(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (args[0].equalsIgnoreCase("emojis")) {
            SocialMenuContext context = SocialMenuContext.builder()
                .viewer(socialPlayer)
                .build();

            EmojiDictionaryMenu dictionary = new EmojiDictionaryMenu();
            dictionary.open(context);
        } else if (args[0].equalsIgnoreCase("keywords")) {
            SocialMenuContext context = SocialMenuContext.builder()
                .viewer(socialPlayer)
                .build();

            KeywordDictionaryMenu dictionary = new KeywordDictionaryMenu();
            dictionary.open(context);
        } else {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getInvalidCommand(), Social.get().getConfig().getMessages().getChannelType());
        }

    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        if (args.length == 1)
            return List.of("emojis", "keywords");
        
        return List.of();
    }
    
}
