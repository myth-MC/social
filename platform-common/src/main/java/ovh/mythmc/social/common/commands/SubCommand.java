package ovh.mythmc.social.common.commands;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.function.BiConsumer;

public interface SubCommand extends BiConsumer<CommandSender, String[]> {

    List<String> tabComplete(CommandSender commandSender, String[] args);

}
