package ovh.mythmc.social.api.util.registry;

import org.jetbrains.annotations.NotNull;

/**
 * The {@code RegistryKey} interface represents a key that can be used within a {@link Registry} to identify
 * and access registered values.
 * 
 * <p>The {@code RegistryKey} is a marker interface for different types of keys that can be used in a registry.
 * Implementations of this interface define unique ways to identify entries in a registry. The key could be 
 * either identified by a unique identifier or by a namespace and key combination.
 * 
 * <p>This interface provides static factory methods to easily create two types of registry keys:
 * <ul>
 *     <li>Identified keys with a unique identifier (e.g., "someKey")</li>
 *     <li>Namespaced keys that have both a namespace and a key (e.g., "namespace:key")</li>
 * </ul>
 * 
 * @see IdentifiedRegistryKey
 * @see NamespacedRegistryKey
 */
public interface RegistryKey {

    /**
     * Creates an {@link IdentifiedRegistryKey} using the given unique identifier.
     * 
     * @param identifier the unique identifier for the registry key
     * @return a new instance of {@link IdentifiedRegistryKey}
     */
    static IdentifiedRegistryKey identified(@NotNull String identifier) {
        return new IdentifiedRegistryKey(identifier);
    }

    /**
     * Creates a {@link NamespacedRegistryKey} using the given namespace and key.
     * 
     * @param namespace the namespace that the key belongs to
     * @param key the unique key within the namespace
     * @return a new instance of {@link NamespacedRegistryKey}
     */
    static NamespacedRegistryKey namespaced(@NotNull String namespace, @NotNull String key) {
        return new NamespacedRegistryKey(namespace, key);
    }

    /**
     * Creates a {@link NamespacedRegistryKey} using a namespaced key in the format "namespace:key".
     * 
     * @param namespacedKey the namespaced key in the format "namespace:key"
     * @return a new instance of {@link NamespacedRegistryKey}
     */
    static NamespacedRegistryKey namespaced(@NotNull String namespacedKey) {
        return new NamespacedRegistryKey(namespacedKey);
    }
}