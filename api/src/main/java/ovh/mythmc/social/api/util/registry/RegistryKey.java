package ovh.mythmc.social.api.util.registry;

import org.jetbrains.annotations.NotNull;

public interface RegistryKey {

    static IdentifiedRegistryKey identified(@NotNull String identifier) {
        return new IdentifiedRegistryKey(identifier);
    }

    static NamespacedRegistryKey namespaced(@NotNull String namespace, @NotNull String key) {
        return new NamespacedRegistryKey(namespace, key);
    }

    static NamespacedRegistryKey namespaced(@NotNull String namespacedKey) {
        return new NamespacedRegistryKey(namespacedKey);
    }

    static <T> TypeRegistryKey<T> type(@NotNull Class<T> type) {
        return new TypeRegistryKey<>(type);
    }

}
