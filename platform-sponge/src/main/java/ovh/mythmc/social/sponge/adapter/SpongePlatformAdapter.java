package ovh.mythmc.social.sponge.adapter;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import ovh.mythmc.social.api.configuration.section.settings.ServerLinksSettings;
import ovh.mythmc.social.api.network.channel.NetworkChannelWrapper;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.common.adapter.PlatformAdapter;
import ovh.mythmc.social.sponge.api.SpongeSocialUser;

import java.util.Collection;

public final class SpongePlatformAdapter extends PlatformAdapter {

    @Override
    public void registerPayloadChannel(@NotNull NetworkChannelWrapper channel) {

    }

    @Override
    public void sendServerLinks(@NotNull AbstractSocialUser user, @NotNull Collection<ServerLinksSettings.ServerLink> links) {
    }

    @Override
    public void sendAutoCompletions(@NotNull AbstractSocialUser user, @NotNull Collection<String> autoCompletions) {

    }

    @Override
    public void sendChatMessage(@NotNull AbstractSocialUser user, @NotNull String message) {
        SpongeSocialUser.from(user).player().ifPresent(player -> {
            player.simulateChat(Component.text(message), Cause.of(EventContext.empty(), "sim"));
        });
    }

}
