package ovh.mythmc.social.common.adapter;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.configuration.section.settings.ServerLinksSettings.ServerLink;
import ovh.mythmc.social.api.user.AbstractSocialUser;

public abstract class PlatformAdapter {

    private static PlatformAdapter instance;

    public static void set(PlatformAdapter p) {
        instance = p;
    }

    public static PlatformAdapter get() {
        return instance;
    }

    public abstract boolean canAssignNickname(@NotNull AbstractSocialUser user, @NotNull String nickname);

    public abstract void registerS2CPayloadChannel(@NotNull String channel);

    public abstract void registerC2SPayloadChannel(@NotNull String channel);

    public abstract void sendServerLinks(@NotNull AbstractSocialUser user, @NotNull Collection<ServerLink> links);

    public abstract void sendAutoCompletions(@NotNull AbstractSocialUser user, @NotNull Collection<String> autoCompletions);
    
}
