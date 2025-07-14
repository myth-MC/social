package ovh.mythmc.social.api.util.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface Registry<R extends RegistryKey, T> extends Iterable<T> {

    static <T> Registry<IdentifiedRegistryKey, T> identified(@NotNull Class<T> type) {
        return new AbstractRegistry.Identified<>();
    }

    static <T> NamespacedRegistry<T> namespaced(@NotNull Class<T> type) {
        return new AbstractRegistry.Namespaced<>();
    }

    static <R, T> Registry<TypeRegistryKey<R>, T> type(@NotNull Class<R> registryKeyType, @NotNull Class<T> type) {
        return new AbstractRegistry.Type<>();
    }

    static <T> Registry<TypeRegistryKey<T>, T> type(@NotNull Class<T> type) {
        return new AbstractRegistry.Type<>();
    }

    @NotNull Map<R, T> registry();

    void register(@NotNull R registryKey, @NotNull T value);

    @Nullable T unregister(@NotNull R registryKey);

    boolean containsKey(@NotNull R registryKey);

    boolean containsValue(@NotNull T value);

    @NotNull Optional<T> value(@NotNull R registryKey);

    @NotNull <V extends T> List<V> valuesByType(@NotNull Class<V> type);

    @NotNull Set<R> keys();

    @NotNull List<T> values();

}
