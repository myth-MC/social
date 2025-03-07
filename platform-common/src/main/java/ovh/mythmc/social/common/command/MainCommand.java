package ovh.mythmc.social.common.command;

import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;

public interface MainCommand<C> {

    boolean canRegister();

    void register(@NotNull CommandManager<C> commandManager);
    
}
