package ovh.mythmc.social.api.user.platform;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import ovh.mythmc.social.api.user.SocialUser;

public abstract class PlatformUsers {
    
    private static PlatformUsers instance;

    public static PlatformUsers get() { return instance; }

    public static void set(@NotNull PlatformUsers p) { instance = p; }

    public abstract Collection<SocialUser> onlineUsers();

    public abstract Audience audience(@NotNull SocialUser user);

    public abstract String name(@NotNull SocialUser user);

    public abstract void name(@NotNull SocialUser user, @NotNull String name);

    public abstract boolean isOnline(@NotNull SocialUser user);

    public abstract boolean checkPermission(@NotNull SocialUser user, String permission);

    public abstract void sendCustomPayload(@NotNull SocialUser user, String channel, byte[] bytes);

}
