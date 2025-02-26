package ovh.mythmc.social.paper.adventure;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;

public final class PaperAdventureProvider extends SocialAdventureProvider {

    @Override
    public Audience player(@NotNull Player player) {
        return player;
    }

    @Override
    public Audience console() {
        return Bukkit.getConsoleSender();
    }

    @Override
    public Audience sender(@NotNull CommandSender sender) {
        return sender;
    }
    
}
