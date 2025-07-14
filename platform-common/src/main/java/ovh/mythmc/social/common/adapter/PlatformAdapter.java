package ovh.mythmc.social.common.adapter;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.configuration.section.settings.ServerLinksSettings.ServerLink;
import ovh.mythmc.social.api.network.channel.NetworkChannelWrapper;
import ovh.mythmc.social.api.user.AbstractSocialUser;

public abstract class PlatformAdapter {

    private static PlatformAdapter instance;

    public static void set(PlatformAdapter p) {
        instance = p;
    }

    public static PlatformAdapter get() {
        return instance;
    }

    public abstract void registerPayloadChannel(final @NotNull NetworkChannelWrapper channel);

    public abstract void sendServerLinks(@NotNull AbstractSocialUser user, @NotNull Collection<ServerLink> links);

    public abstract void sendAutoCompletions(@NotNull AbstractSocialUser user, @NotNull Collection<String> autoCompletions);

    public abstract void sendChatMessage(@NotNull AbstractSocialUser user, @NotNull String message);

}
