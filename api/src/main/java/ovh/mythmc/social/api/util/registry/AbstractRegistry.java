package ovh.mythmc.social.api.util.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractRegistry<R extends RegistryKey, T> implements Registry<R, T> {

    @Override
    public @NotNull Iterator<T> iterator() {
        return values().iterator();
    }

    @Override
    public void register(@NotNull R registryKey, @NotNull T value) {
        registry().put(registryKey, value);
    }

    @Override
    public @Nullable T unregister(@NotNull R registryKey) {
        return registry().remove(registryKey);
    }

    @Override
    public boolean containsKey(@NotNull R registryKey) {
        return registry().containsKey(registryKey);
    }

    @Override
    public boolean containsValue(@NotNull T value) {
        return registry().containsValue(value);
    }

    @Override
    public @NotNull Optional<T> value(@NotNull R registryKey) {
        return Optional.ofNullable(registry().get(registryKey));
    }

    @Override
    public @NotNull <V extends T> List<V> valuesByType(@NotNull Class<V> type) {
        return registry().values().stream()
            .filter(type::isInstance)
            .map(value -> (V) value)
            .toList();
    }

    @Override
    public @NotNull Set<R> keys() {
        return registry().keySet();
    }

    @Override
    public @NotNull List<T> values() {
        return registry().values().stream().toList();
    }

    public static class Identified<T> extends AbstractRegistry<IdentifiedRegistryKey, T> {

        private final Map<IdentifiedRegistryKey, T> registry = new HashMap<>();

        protected Identified() { }

        @Override
        public @NotNull Map<IdentifiedRegistryKey, T> registry() {
            return this.registry;
        }

    }

    public static class Namespaced<T> extends AbstractRegistry<NamespacedRegistryKey, T> implements NamespacedRegistry<T> {

        private final Map<NamespacedRegistryKey, T> registry = new HashMap<>();

        protected Namespaced() { }

        @Override
        public @NotNull Map<NamespacedRegistryKey, T> registry() {
            return this.registry;
        }

        @Override
        public @NotNull List<T> valuesByNamespaceComponent(@NotNull String namespaceComponent) {
            return registry().entrySet().stream()
                .filter(entry -> entry.getKey().namespace().equalsIgnoreCase(namespaceComponent))
                .map(Map.Entry::getValue)
                .toList();
        }

        @Override
        public @NotNull List<T> valuesByKeyComponent(@NotNull String keyComponent) {
            return registry().entrySet().stream()
                .filter(entry -> entry.getKey().key().equalsIgnoreCase(keyComponent))
                .map(Map.Entry::getValue)
                .toList();
        }
        
    }

    public static class Type<R, T> extends AbstractRegistry<TypeRegistryKey<R>, T> {

        private final Map<TypeRegistryKey<R>, T> registry = new HashMap<>();

        protected Type() { }

        @Override
        public @NotNull Map<TypeRegistryKey<R>, T> registry() {
            return this.registry;
        }

    }

}
