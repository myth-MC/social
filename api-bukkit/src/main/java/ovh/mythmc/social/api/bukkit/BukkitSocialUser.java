package ovh.mythmc.social.api.bukkit;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import ovh.mythmc.social.api.reaction.Reaction;
import ovh.mythmc.social.api.user.AbstractSocialUser;

public final class BukkitSocialUser extends AbstractSocialUser<Player> {

    protected BukkitSocialUser(UUID uuid, Player player, String name) {
        super(uuid, player, name);
    }

    public static BukkitSocialUser from(AbstractSocialUser<? extends Object> user) {
        if (user instanceof BukkitSocialUser bukkitSocialUser)
            return bukkitSocialUser;

        return null;
    }

    public static BukkitSocialUser from(Player player) {
        return SocialBukkit.get().getUserService().map(player);
    }

    public static BukkitSocialUser from(@NotNull UUID uuid) {
        return SocialBukkit.get().getUserService().getByUuid(uuid).orElse(null);
    }

    @Override
    public Audience audience() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'audience'");
    }

    @Override
    public String name() {
        return player.getName();
    }

    @Override
    public void name(@NotNull String name) {
        player.setDisplayName(name);
    }

    @Override
    public boolean checkPermission(@NotNull String permission) {
        return player.hasPermission(permission);
    }

    @Override
    protected void sendCustomPayload(String channel, byte[] payload) {
        player.sendPluginMessage(Bukkit.getPluginManager().getPlugin("social"), channel, payload);
    }

    @Override
    public void playReaction(@NotNull Reaction reaction) {
        SocialBukkit.get().getReactionFactory().scheduleReaction(this, reaction);
    }
    
}
