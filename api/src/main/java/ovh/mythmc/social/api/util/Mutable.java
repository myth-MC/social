package ovh.mythmc.social.api.util;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Mutable<T> extends Serializable {

    static <T> Mutable<T> of(T object) {
        return new MutableImpl<>(object);
    }

    static <T> Mutable<T> of(@NotNull Optional<T> optionalObject) {
        return new MutableImpl<T>(optionalObject.orElse(null));
    }

    static <T, R> Mutable<T> referable(T object, @NotNull Class<R> referenceType, @NotNull Function<R, @NotNull T> map,
            @NotNull Function<T, @NotNull R> reverse) {
        return new MutableReferableImpl<>(object, map, reverse);
    }

    static <T> Mutable<T> empty() {
        return new MutableImpl<>(null);
    }

    T get();

    void set(T object);

    boolean isEmpty();

    boolean isPresent();

    void ifPresent(@NotNull Consumer<T> object);

    @NotNull
    Mutable.Subscription onChange(@NotNull BiConsumer<T, T> onChange);

    default @NotNull Mutable<T> or(@NotNull Mutable<T> other) {
        return isPresent() ? this : other;
    }

    public sealed interface Subscription permits MutableImpl.SubscriptionImpl {

        void unsubscribe();

    }

}
