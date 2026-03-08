package ovh.mythmc.social.api.util.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The {@code Registry} interface represents a generic registry for storing and managing a collection of
 * values, which are associated with keys that implement the {@link RegistryKey} interface.
 * 
 * <p>Registries provide a way to store, retrieve, and manipulate objects based on their unique keys.
 * The registry can store any type of object that is mapped to a particular key type {@code R}, which
 * extends {@link RegistryKey}. Implementations of this interface will define specific behaviors for
 * managing the registry, including registration, unregistration, and key-based lookups.
 * 
 * <p>It provides methods for:
 * <ul>
 *     <li>Registering objects to the registry</li>
 *     <li>Unregistering objects</li>
 *     <li>Checking if a key or value exists</li>
 *     <li>Retrieving values associated with keys</li>
 *     <li>Retrieving values by their type</li>
 * </ul>
 * 
 * @param <R> the type of the registry key, extending {@link RegistryKey}.
 * @param <T> the type of the values stored in the registry.
 * @see RegistryKey
 */
public interface Registry<R extends RegistryKey, T> extends Iterable<T> {

    /**
     * Creates and returns a registry that uses {@link IdentifiedRegistryKey} as the key type.
     * 
     * @param type the type of the values stored in the registry.
     * @param <T> the type of the values.
     * @return a registry that uses identified keys.
     */
    static <T> Registry<IdentifiedRegistryKey, T> identified(@NotNull Class<T> type) {
        return new AbstractRegistry.Identified<>();
    }

    /**
     * Creates and returns a registry that uses {@link NamespacedRegistryKey} as the key type.
     * 
     * @param type the type of the values stored in the registry.
     * @param <T> the type of the values.
     * @return a registry that uses namespaced keys.
     */
    static <T> NamespacedRegistry<T> namespaced(@NotNull Class<T> type) {
        return new AbstractRegistry.Namespaced<>();
    }

    /**
     * Retrieves the underlying map of the registry, where the keys are the registry keys
     * and the values are the associated objects.
     * 
     * @return a {@link Map} where keys are registry keys and values are the registry values.
     */
    @NotNull Map<R, T> registry();

    /**
     * Registers a value to the registry, associating it with the given registry key.
     * 
     * @param registryKey the key to associate with the value.
     * @param value the value to register.
     */
    void register(@NotNull R registryKey, @NotNull T value);

    /**
     * Unregisters a value from the registry, removing it associated with the given registry key.
     * 
     * @param registryKey the key whose associated value should be removed.
     * @return the removed value, or {@code null} if no value was associated with the key.
     */
    @Nullable T unregister(@NotNull R registryKey);

    /**
     * Checks if a value exists in the registry for the given registry key.
     * 
     * @param registryKey the key to check.
     * @return {@code true} if the key exists in the registry, {@code false} otherwise.
     */
    boolean containsKey(@NotNull R registryKey);

    /**
     * Checks if a given value exists in the registry.
     * 
     * @param value the value to check.
     * @return {@code true} if the value exists in the registry, {@code false} otherwise.
     */
    boolean containsValue(@NotNull T value);

    /**
     * Retrieves the value associated with the given registry key, wrapped in an {@link Optional}.
     * 
     * @param registryKey the key whose associated value to retrieve.
     * @return an {@link Optional} containing the associated value, or an empty {@link Optional} if no value
     *         is associated with the key.
     */
    @NotNull Optional<T> value(@NotNull R registryKey);

    /**
     * Retrieves a list of values of a specific type associated with the registry.
     * 
     * @param type the class type of the values to retrieve.
     * @param <V> the type of the values to retrieve.
     * @return a list of values that match the given type.
     */
    @NotNull <V extends T> List<V> valuesByType(@NotNull Class<V> type);

    /**
     * Retrieves a set of all the registry keys in the registry.
     * 
     * @return a {@link Set} of registry keys.
     */
    @NotNull Set<R> keys();

    /**
     * Retrieves a list of all the values in the registry.
     * 
     * @return a {@link List} of all values in the registry.
     */
    @NotNull List<T> values();
}