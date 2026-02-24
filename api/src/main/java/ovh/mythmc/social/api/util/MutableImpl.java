package ovh.mythmc.social.api.util;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

class MutableImpl<T> implements Mutable<T> {

    final List<BiConsumer<T, T>> listeners = new CopyOnWriteArrayList<>();

    volatile T object;

    MutableImpl(T object) {
        this.object = object;
    }

    @Override
    public T get() {
        return object;
    }

    @Override
    public void set(T object) {
        final T oldValue;
        final T newValue;

        synchronized (this) {
            if (Objects.equals(this.object, object))
                return;

            oldValue = this.object;
            this.object = object;
            newValue = object;
        }

        // Call listeners outside synchronized block
        this.listeners.forEach(listener -> listener.accept(oldValue, newValue));
    }

    @Override
    public boolean isEmpty() {
        return this.object == null;
    }

    @Override
    public boolean isPresent() {
        return !isEmpty();
    }

    @Override
    public void ifPresent(@NotNull Consumer<T> consumer) {
        if (isPresent())
            consumer.accept(this.object);
    }

    @Override
    public @NotNull Mutable.Subscription onChange(@NotNull BiConsumer<T, T> onChange) {
        listeners.add(onChange);
        return new SubscriptionImpl(() -> listeners.remove(onChange));
    }

    @Override
    public boolean equals(Object object1) {
        if (object1 == null || getClass() != object1.getClass())
            return false;
        MutableImpl<?> mutable = (MutableImpl<?>) object1;
        return Objects.equals(object, mutable.object);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(object);
    }

    final class SubscriptionImpl implements Mutable.Subscription {

        private final Runnable unsubscribe;

        SubscriptionImpl(Runnable unsubscribe) {
            this.unsubscribe = unsubscribe;
        }

        @Override
        public void unsubscribe() {
            unsubscribe.run();
        }

    }

}
