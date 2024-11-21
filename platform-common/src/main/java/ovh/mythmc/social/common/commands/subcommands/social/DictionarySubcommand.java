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

public class DictionarySubcommand implements SubCommand {

    @Override
    public void accept(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            String message = Social.get().getConfig().getMessages().getErrors().getCannotBeRunFromConsole();
            SocialAdventureProvider.get().sender(commandSender).sendMessage(MiniMessage.miniMessage().deserialize(message));
            return;
        }

        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(((Player) commandSender).getUniqueId());
        if (socialPlayer == null)
            return;

        SocialMenuContext context = SocialMenuContext.builder()
            .viewer(socialPlayer)
            .build();

        EmojiDictionaryMenu dictionary = new EmojiDictionaryMenu();
        dictionary.open(context);
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        return List.of();
    }
    
}
