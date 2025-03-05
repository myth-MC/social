package ovh.mythmc.social.api.bukkit;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.user.SocialUserService;

public abstract class SocialBukkit implements Social<Player, BukkitSocialUser> {

    private static SocialBukkit instance;

    public static void set(@NotNull SocialBukkit s) {
        instance = s;
    }

    public static SocialBukkit get() {
        return instance;
    }

    @Override
    public @NotNull SocialUserService<Player, BukkitSocialUser> getUserService() {
        return BukkitSocialUserService.instance;
    }

    
    
}
