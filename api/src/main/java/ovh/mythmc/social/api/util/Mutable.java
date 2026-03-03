package ovh.mythmc.social.api.util;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The {@code Mutable} interface represents an object that holds a value of type {@code T}, which can be 
 * modified after it is created. It provides a mechanism to wrap values in a mutable container with methods
 * to retrieve, modify, and monitor changes to the value.
 * 
 * <p>This interface supports several ways to create mutable containers:
 * <ul>
 *     <li>{@link #of(Object)} for wrapping an object</li>
 *     <li>{@link #of(Optional)} for wrapping an {@link Optional} value</li>
 *     <li>{@link #referable(Object, Class, Function, Function)} for wrapping an object with reference mappings</li>
 *     <li>{@link #empty()} for creating an empty container</li>
 * </ul>
 * 
 * <p>In addition, it provides various methods to interact with the contained value, including checking if
 * the value is present, setting the value, and executing actions when the value changes.
 * 
 * @param <T> the type of the value contained in the {@code Mutable}
 */
public interface Mutable<T> extends Serializable {

    /**
     * Creates a {@code Mutable} container that holds the given object.
     * 
     * @param object the object to be wrapped in the {@code Mutable}
     * @param <T> the type of the object
     * @return a new {@code Mutable} containing the given object
     */
    static <T> Mutable<T> of(T object) {
        return new MutableImpl<>(object);
    }

    /**
     * Creates a {@code Mutable} container from an {@link Optional} value.
     * 
     * @param optionalObject the {@link Optional} to wrap
     * @param <T> the type of the object inside the {@link Optional}
     * @return a new {@code Mutable} containing the value inside the {@link Optional}, or {@code null} if empty
     */
    static <T> Mutable<T> of(@NotNull Optional<T> optionalObject) {
        return new MutableImpl<T>(optionalObject.orElse(null));
    }

    /**
     * Creates a {@code Mutable} container that is "referable", meaning that it can be mapped from one type to 
     * another and vice versa using the provided mapping functions.
     * 
     * @param object the object to wrap in the {@code Mutable}
     * @param referenceType the class type of the reference object
     * @param map a function to map the reference type to the actual object
     * @param reverse a function to map the object back to the reference type
     * @param <T> the type of the object to wrap
     * @param <R> the type of the reference type used for mapping
     * @return a new {@code Mutable} with mapping functions applied
     */
    static <T, R> Mutable<T> referable(T object, @NotNull Class<R> referenceType, @NotNull Function<R, @NotNull T> map,
            @NotNull Function<T, @NotNull R> reverse) {
        return new MutableReferableImpl<>(object, map, reverse);
    }

    /**
     * Creates an empty {@code Mutable} container with a {@code null} value.
     * 
     * @param <T> the type of the value (which will be {@code null})
     * @return a new empty {@code Mutable}
     */
    static <T> Mutable<T> empty() {
        return new MutableImpl<>(null);
    }

    /**
     * Retrieves the current value held by the {@code Mutable}.
     * 
     * @return the current value
     */
    T get();

    /**
     * Sets a new value for the {@code Mutable}.
     * 
     * @param object the new value to set
     */
    void set(T object);

    /**
     * Checks if the value held by the {@code Mutable} is {@code null}.
     * 
     * @return {@code true} if the value is {@code null}, otherwise {@code false}
     */
    boolean isEmpty();

    /**
     * Checks if the {@code Mutable} contains a non-null value.
     * 
     * @return {@code true} if the value is non-null, otherwise {@code false}
     */
    boolean isPresent();

    /**
     * Executes the given consumer if the value is present (i.e., not {@code null}).
     * 
     * @param object the consumer to execute with the current value
     */
    void ifPresent(@NotNull Consumer<T> object);

    /**
     * Registers a {@link BiConsumer} to monitor changes to the value. The consumer will be called when the value
     * is updated, providing the old value and the new value.
     * 
     * @param onChange the {@link BiConsumer} to call when the value changes
     * @return a {@link Subscription} that can be used to unsubscribe from change notifications
     */
    @NotNull
    Mutable.Subscription onChange(@NotNull BiConsumer<T, T> onChange);

    /**
     * Returns the current value if it is present; otherwise, returns the provided fallback value.
     * 
     * @param other the value to return if the current value is not present
     * @return the current value if present, otherwise the fallback value
     */
    default @NotNull T or(@NotNull T other) {
        return isPresent() ? get() : other;
    }

    /**
     * The {@code Subscription} interface represents a way to unsubscribe from change notifications on a 
     * {@code Mutable} object.
     * 
     * <p>Implementations of this interface allow unsubscribing from the {@link onChange} notifications
     * once they are no longer needed.
     */
    public sealed interface Subscription permits MutableImpl.SubscriptionImpl {

        /**
         * Unsubscribes from the change notifications.
         */
        void unsubscribe();
    }
}