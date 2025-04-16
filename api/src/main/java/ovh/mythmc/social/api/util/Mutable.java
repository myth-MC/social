package ovh.mythmc.social.api.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Mutable<T> {

    static <T> Mutable<T> of(@NotNull T object) {
        return new MutableImpl<>(object);
    }

    static <T> Mutable<T> callback(@NotNull T object, @NotNull BiConsumer<T, T> callback) {
        return new MutableCallbackImpl<>(object, callback);
    }

    static <T> Mutable<T> empty() {
        return new MutableImpl<>(null);
    }

    T get();

    void set(T object);

    boolean isEmpty();

    boolean isPresent();

    void ifPresent(@NotNull Consumer<T> object);

}
