package ovh.mythmc.social.api.network.channel.identifier;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class NetworkChannelIdentifier {

    public static NetworkChannelIdentifier of(final @NotNull String namespace, final @NotNull String identifier) {
        return new NetworkChannelIdentifier(namespace, identifier);
    }

    public static NetworkChannelIdentifier of(final @NotNull String identifier) {
        return of("social", identifier);
    }

    private final String namespace;

    private final String identifier;

    private NetworkChannelIdentifier(final @NotNull String namespace, final @NotNull String identifier) {
        this.namespace = namespace;
        this.identifier = identifier;
    }

    public String namespace() {
        return this.namespace;
    }

    public String identifier() {
        return this.identifier;
    }

    @Override
    public String toString() {
        return this.namespace + ":" + this.identifier;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        NetworkChannelIdentifier that = (NetworkChannelIdentifier) object;
        return Objects.equals(namespace, that.namespace) && Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, identifier);
    }

}
