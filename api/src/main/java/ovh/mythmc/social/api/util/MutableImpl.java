package ovh.mythmc.social.api.util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

class MutableImpl<T> implements Mutable<T> {

    T object;

    MutableImpl(T object) {
        this.object = object;
    }

    @Override
    public T get() {
        return object;
    }

    @Override
    public void set(T object) {
        this.object = object;
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
    public void ifPresent(@NotNull Consumer<T> object) {
        if (isPresent())
            object.accept(this.object);
    }

    @Override
    public boolean equals(Object object1) {
        if (object1 == null || getClass() != object1.getClass()) return false;
        MutableImpl<?> mutable = (MutableImpl<?>) object1;
        return Objects.equals(object, mutable.object);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(object);
    }

    @Override
    public String toString() {
        return "MutableImpl{" +
            "object=" + object +
            '}';
    }

}
