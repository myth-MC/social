package ovh.mythmc.social.api.util.registry;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NamespacedRegistryKey implements RegistryKey {

    private final String namespace;

    private final String key;

    NamespacedRegistryKey(@NotNull String namespacedKey) {
        validateNamespacedKey(namespacedKey);

        this.namespace = namespacedKey.substring(0, namespacedKey.indexOf(":"));
        this.key = namespacedKey.substring(namespacedKey.indexOf(":") + 1);
    }

    NamespacedRegistryKey(@NotNull String namespace, @NotNull String key) {
        validateComponent(namespace);
        validateComponent(key);

        this.namespace = namespace;
        this.key = key;
    }

    public @NotNull String namespace() {
        return this.namespace;
    }

    public @NotNull String key() {
        return this.key;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        NamespacedRegistryKey that = (NamespacedRegistryKey) object;
        return Objects.equals(namespace, that.namespace) && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, key);
    }

    @Override
    public String toString() {
        return "NamespacedRegistryKey{" +
            "namespace='" + namespace + '\'' +
            ", key='" + key + '\'' +
            '}';
    }

    private static void validateComponent(@NotNull String keyComponent) {
        if (keyComponent.contains(" "))
            throw new IllegalArgumentException("Namespaced keys cannot contain space characters");

        if (keyComponent.isBlank())
            throw new IllegalArgumentException("Namespaced keys cannot be empty");
    }

    private static void validateNamespacedKey(@NotNull String namespacedKey) {
        validateComponent(namespacedKey);
        if (!namespacedKey.contains(":"))
            throw new IllegalArgumentException("Namespaced keys must be provided in the correct format: namespace:key");
    }

}
