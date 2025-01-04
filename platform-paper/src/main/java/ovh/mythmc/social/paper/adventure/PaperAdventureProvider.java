package ovh.mythmc.social.paper.adventure;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.chat.ChannelType;

public final class PaperAdventureProvider extends SocialAdventureProvider {

    @Override
    public void sendMessage(@NotNull Player player, @NotNull ComponentLike message, @NotNull ChannelType type) {
        switch (type) {
            case CHAT -> player.sendMessage(message);
            case ACTION_BAR -> player.sendActionBar(message);
        }
    }

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
