package ovh.mythmc.social.api.util.registry;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface NamespacedRegistry<T> extends Registry<NamespacedRegistryKey, T> {

    @NotNull List<T> valuesByNamespaceComponent(@NotNull String namespaceComponent);

    @NotNull List<T> valuesByKeyComponent(@NotNull String keyComponent);

    default @NotNull Set<String> namespaceComponents() {
        return keys().stream()
            .map(NamespacedRegistryKey::namespace)
            .collect(Collectors.toUnmodifiableSet());
    }

    default @NotNull Set<String> keyComponents() {
        return keys().stream()
            .map(NamespacedRegistryKey::key)
            .collect(Collectors.toSet());
    }

}
