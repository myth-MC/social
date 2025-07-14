package ovh.mythmc.social.common.command;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;

public interface SubCommand<C> {

    void register(@NotNull CommandManager<C> commandManager, @NotNull Command.Builder<C> command);
    
}
