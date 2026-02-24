package ovh.mythmc.social.api.user;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.network.channel.S2CNetworkChannelWrapper;
import ovh.mythmc.social.api.network.payload.NetworkPayloadWrapper.ServerToClient;

public class ConsoleSocialUser extends AbstractSocialUser {

    private static ConsoleSocialUser INSTANCE = new ConsoleSocialUser();

    public static @NotNull ConsoleSocialUser get() {
        return INSTANCE;
    }

    public static @NotNull ConsoleSocialUser get(@NotNull ChatChannel channel) {
        ConsoleSocialUser console = new ConsoleSocialUser();
        console.mainChannel.set(channel);
        return console;
    }

    private final static UUID uuid = UUID.nameUUIDFromBytes("#CONSOLE".getBytes(StandardCharsets.UTF_8));
    private final static String username = "CONSOLE";

    protected ConsoleSocialUser() {
        super(uuid, username, ConsoleSocialUser.class);

        // Configure extra parameters
        this.socialSpy.set(true);
    }

    @Override
    public boolean checkPermission(@NotNull String permission) {
        return true;
    }

    @Override
    public <T extends ServerToClient> void sendCustomPayload(@NotNull S2CNetworkChannelWrapper<T> channel,
            @NotNull T payload) {

    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public @NotNull Audience audience() {
        return SocialAdventureProvider.get().console();
    }

}
