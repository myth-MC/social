package ovh.mythmc.social.api.util.registry;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TypeRegistryKey<T> implements RegistryKey {

    private final Class<T> type;

    TypeRegistryKey(@NotNull Class<T> type) {
        this.type = type;
    }

    public @NotNull Class<T> type() {
        return this.type;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        TypeRegistryKey<?> that = (TypeRegistryKey<?>) object;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type);
    }

    @Override
    public String toString() {
        return "TypeRegistryKey{" +
            "type=" + type +
            '}';
    }

}
