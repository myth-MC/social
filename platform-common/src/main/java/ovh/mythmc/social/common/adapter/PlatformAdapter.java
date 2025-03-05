package ovh.mythmc.social.common.adapter;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.configuration.section.settings.ServerLinksSettings.ServerLink;
import ovh.mythmc.social.api.user.AbstractSocialUser;

public abstract class PlatformAdapter<U extends AbstractSocialUser<? extends Object>> {

    private static PlatformAdapter<AbstractSocialUser<? extends Object>> instance;

    public static void set(PlatformAdapter<AbstractSocialUser<? extends Object>> p) {
        instance = p;
    }

    public static PlatformAdapter<AbstractSocialUser<? extends Object>> get() {
        return instance;
    }

    public abstract void registerS2CPayloadChannel(@NotNull String channel);

    public abstract void registerC2SPayloadChannel(@NotNull String channel);

    public abstract void sendServerLinks(@NotNull U user, @NotNull Collection<ServerLink> links);

    public abstract void sendAutoCompletions(@NotNull U user, @NotNull Collection<String> autoCompletions);
    
}
