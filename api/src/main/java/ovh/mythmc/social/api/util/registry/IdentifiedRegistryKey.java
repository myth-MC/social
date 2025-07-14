package ovh.mythmc.social.api.util.registry;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class IdentifiedRegistryKey implements RegistryKey {

    private final String identifier;

    IdentifiedRegistryKey(@NotNull String identifier) {
        this.identifier = identifier;
    }

    public @NotNull String identifier() {
        return this.identifier;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        IdentifiedRegistryKey that = (IdentifiedRegistryKey) object;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }

    @Override
    public String toString() {
        return "SimpleRegistryKey{" +
            "identifier='" + identifier + '\'' +
            '}';
    }

    private static void validate(@NotNull String identifier) {
        if (identifier.contains(" "))
            throw new IllegalArgumentException("Identified keys cannot contain space characters");

        if (identifier.isBlank())
            throw new IllegalArgumentException("Identified keys cannot be empty");
    }

}
