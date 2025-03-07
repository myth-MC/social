package ovh.mythmc.social.api.bukkit;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.reaction.Reaction;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@Getter
@Accessors(fluent = true)
public final class BukkitSocialUser extends AbstractSocialUser {

    BukkitSocialUser() {
    }

    protected BukkitSocialUser(UUID uuid, String name) {
        super(uuid, name);
    }

    public static BukkitSocialUser from(AbstractSocialUser user) {
        if (user == null)
            return null;

        if (user instanceof BukkitSocialUser bukkitSocialUser)
            return bukkitSocialUser;

        return null;
    }

    public static BukkitSocialUser from(Player player) {
        return from(player.getUniqueId());
    }

    public static BukkitSocialUser from(@NotNull UUID uuid) {
        return from(Social.get().getUserService().getByUuid(uuid).orElse(null));
    }

    @Override
    public Audience audience() {
        return SocialAdventureProvider.get().user(this);
    }

    @Override
    public String name() {
        return player().get().getName();
    }

    @Override
    public void name(@NotNull String name) {
        player().get().setDisplayName(name);
    }

    @Override
    public boolean checkPermission(@NotNull String permission) {
        return player().get().hasPermission(permission);
    }

    @Override
    public boolean isOnline() {
        return player() != null;
    }

    @Override
    protected void sendCustomPayload(String channel, byte[] payload) {
        player().get().sendPluginMessage(Bukkit.getPluginManager().getPlugin("social"), channel, payload);
    }

    @Override
    public void playReaction(@NotNull Reaction reaction) {
        Social.get().getReactionFactory().play(this, reaction);
    }

    public Optional<Player> player() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }
    
}
