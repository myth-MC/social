package ovh.mythmc.social.sponge.api;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.network.channel.S2CNetworkChannelWrapper;
import ovh.mythmc.social.api.network.payload.NetworkPayloadWrapper;
import ovh.mythmc.social.api.reaction.Reaction;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.libs.com.j256.ormlite.table.DatabaseTable;

import java.util.Optional;
import java.util.UUID;

@Getter
@Accessors(fluent = true)
@DatabaseTable(tableName = "users")
public final class SpongeSocialUser extends AbstractSocialUser {

    SpongeSocialUser() {
    }

    SpongeSocialUser(final UUID uuid) {
        super(uuid);
    }

    public static SpongeSocialUser from(final AbstractSocialUser user) {
        if (user == null)
            return null;

        if (user instanceof SpongeSocialUser spongeSocialUser)
            return spongeSocialUser;

        return null;
    }

    public static SpongeSocialUser from(final @NotNull ServerPlayer player) {
        return from(player.uniqueId());
    }

    public static SpongeSocialUser from(final @NotNull UUID uuid) {
        return from(Social.get().getUserService().getByUuid(uuid).orElse(null));
    }

    @Override
    public @NotNull Class<? extends SocialUser> rendererClass() {
        return SpongeSocialUser.class;
    }

    @Override
    public <T extends NetworkPayloadWrapper.ServerToClient> void sendCustomPayload(@NotNull S2CNetworkChannelWrapper<T> channel, @NotNull T payload) {

    }

    @Override
    public void playReaction(@NotNull Reaction reaction) {

    }

    @Override
    public @NotNull Audience audience() {
        return player().get();
    }

    @Override
    public @NotNull String name() {
        return player().get().name();
    }

    @Override
    public void name(@NotNull String name) {
        player().ifPresent(player -> player.displayName().set(Component.text(name)));
    }

    @Override
    public boolean checkPermission(@NotNull String permission) {
        return player().get().hasPermission(permission);
    }

    @Override
    public boolean isOnline() {
        return player().isPresent();
    }

    public Optional<ServerPlayer> player() {
        return Sponge.server().player(uuid);
    }

    public CommandCause toCommandCause() {
        final ServerPlayer player = player().get();
        final Cause cause = Sponge.server().causeStackManager()
            .addContext(EventContextKeys.SUBJECT, player)
            .addContext(EventContextKeys.COMMAND, "")
            .pushCause(player).currentCause();

        return new CommandCause() {
            @Override
            public Cause cause() {
                return cause;
            }

            @Override
            public Subject subject() {
                return player;
            }

            @Override
            public Audience audience() {
                return player;
            }

            @Override
            public Optional<ServerLocation> location() {
                return Optional.ofNullable(player.serverLocation());
            }

            @Override
            public Optional<Vector3d> rotation() {
                return Optional.ofNullable(player.rotation());
            }

            @Override
            public Optional<BlockSnapshot> targetBlock() {
                return Optional.empty();
            }

            @Override
            public void sendMessage(Component message) {
                player.sendMessage(message);
            }

            @Override
            public void sendMessage(Identified source, Component message) {
                player.sendMessage(source, message);
            }

            @Override
            public void sendMessage(Identity source, Component message) {
                player.sendMessage(source, message);
            }
        };
    }

}
