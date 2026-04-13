package ovh.mythmc.social.api.chat.renderer;

import java.util.function.Function;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

import net.kyori.adventure.audience.Audience;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.context.SocialRendererContext;

/**
 * Renders a
 * {@link ovh.mythmc.social.api.context.SocialRegisteredMessageContext} for a
 * specific
 * audience target type {@code T}.
 *
 * <p>
 * Renderers are registered via
 * {@link ovh.mythmc.social.api.chat.ChatManager#registerRenderer(Class, SocialChatRenderer, java.util.function.Function)
 * ChatManager#registerRenderer}. The framework maps incoming
 * {@link net.kyori.adventure.audience.Audience}
 * objects to a target of type {@code T} and then calls
 * {@link #render(Object, SocialRegisteredMessageContext)} to produce a
 * {@link ovh.mythmc.social.api.context.SocialRendererContext}.
 *
 * @param <T> the type of object this renderer operates on (e.g. a player class)
 */
public interface SocialChatRenderer<T> {

    /**
     * Creates a new builder for the given renderer implementation.
     *
     * @param renderer the renderer to wrap
     * @param <T>      the audience target type
     * @return a new builder
     */
    static <T> SocialChatRenderer.Builder<T> builder(@NotNull SocialChatRenderer<T> renderer) {
        return new Builder<T>(renderer);
    }

    /**
     * Renders the message for the given target.
     *
     * @param target  the resolved audience target
     * @param context the message context to render
     * @return the renderer output context
     */
    SocialRendererContext render(@NotNull T target, @NotNull SocialRegisteredMessageContext context);

    /**
     * Builder for {@link Registered} wrappers around a {@link SocialChatRenderer}.
     *
     * @param <T> the audience target type
     */
    final class Builder<T> {

        private final SocialChatRenderer<T> renderer;

        private Predicate<SocialRegisteredMessageContext> renderIf;

        private Function<Audience, MapResult<T>> map;

        private Builder(SocialChatRenderer<T> renderer) {
            this.renderer = renderer;
        }

        /**
         * Sets a predicate that controls whether this renderer runs for a given
         * message.
         *
         * @param renderIf the predicate; the renderer is skipped when this returns
         *                 {@code false}
         * @return this builder
         */
        public Builder<T> renderIf(Predicate<SocialRegisteredMessageContext> renderIf) {
            this.renderIf = renderIf;
            return this;
        }

        /**
         * Sets the function used to map an {@link Audience} to a target of type
         * {@code T}.
         *
         * @param map the mapping function; return {@link MapResult#ignore()} to
         *            silently skip rendering
         * @return this builder
         */
        public Builder<T> map(Function<Audience, MapResult<T>> map) {
            this.map = map;
            return this;
        }

        /**
         * Builds and returns the {@link Registered} wrapper.
         *
         * @return the registered renderer
         */
        public SocialChatRenderer.Registered<T> build() {
            return new Registered<T>(renderer, renderIf, map);
        }

    }

    /**
     * A configured and registered wrapper around a {@link SocialChatRenderer}.
     *
     * @param <T> the audience target type
     */
    final class Registered<T> {

        private final SocialChatRenderer<T> renderer;

        private final Predicate<SocialRegisteredMessageContext> renderIf;

        private final Function<Audience, MapResult<T>> map;

        private Registered(SocialChatRenderer<T> renderer, Predicate<SocialRegisteredMessageContext> renderIf, Function<Audience, MapResult<T>> map) {
            this.renderer = renderer;
            this.renderIf = renderIf;
            this.map = map;
        }

        /**
         * Attempts to map the given audience to a target of type {@code T}.
         *
         * @param audience the audience to map
         * @return a {@link MapResult} that is either a success, ignore, or failure
         */
        public MapResult<T> mapFromAudience(@NotNull Audience audience) {
            if (map == null)
                return MapResult.failure("This renderer cannot map objects!");

            return map.apply(audience);
        }

        /**
         * Renders the message for the audience, first mapping it to a target.
         * Returns {@code null} if mapping fails or should be skipped.
         *
         * @param audience the audience to render for
         * @param context  the message context
         * @return the render result, or {@code null}
         */
        public SocialRendererContext render(@NotNull Audience audience,
                @NotNull SocialRegisteredMessageContext context) {
            var result = mapFromAudience(audience);
            if (result.isIgnored())
                return null;

            if (result instanceof MapResult.Failure<?> failure) {
                Social.get().getLogger().error(failure.getDebugMessage());

                return null;
            }

            return render(result.result(), context);
        }

        /**
         * Renders the message directly for the given target.
         * Returns {@code null} if the {@code renderIf} predicate is not satisfied.
         *
         * @param target  the resolved target
         * @param context the message context
         * @return the render result, or {@code null}
         */
        public SocialRendererContext render(@NotNull T target, @NotNull SocialRegisteredMessageContext context) {
            if (renderIf != null && !renderIf.test(context))
                return null;

            return renderer.render(target, context);
        }

    }

    /**
     * Represents the outcome of mapping an {@link Audience} to a renderer target.
     *
     * <p>
     * Use {@link #success(Object)} when mapping succeeded, {@link #ignore()} to
     * silently
     * skip rendering without logging (e.g. the audience is not a player), or
     * {@link #failure(String)} internally when something unexpected went wrong.
     *
     * @param <T> the renderer target type
     */
    @NonExtendable
    abstract class MapResult<T> {

        /** Returns the mapped target, or {@code null} for ignore/failure results. */
        public abstract @Nullable T result();

        /**
         * Creates a successful mapping result.
         *
         * @param result the mapped target
         * @param <T>    the target type
         * @return a success result
         */
        public static <T> MapResult.Success<T> success(T result) {
            return new Success<>(result);
        }

        /**
         * Creates an ignore result; rendering is skipped silently.
         *
         * @param <T> the target type
         * @return an ignore result
         */
        public static <T> MapResult.Ignore<T> ignore() {
            return new Ignore<T>(null);
        }

        static <T> MapResult.Failure<T> failure(@NotNull String debugMessage) {
            return new Failure<>(null, debugMessage);
        }

        /** Returns {@code true} if this result represents a successful mapping. */
        public boolean isSuccess() {
            return this instanceof MapResult.Success;
        }

        /** Returns {@code true} if this result represents a silent skip. */
        public boolean isIgnored() {
            return this instanceof MapResult.Ignore;
        }

        /**
         * Returns {@code true} if this result represents a mapping failure that should
         * be logged.
         */
        public boolean isFailure() {
            return this instanceof MapResult.Failure;
        }

        private final static class Success<T> extends MapResult<T> {

            private final T result;

            private Success(T result) {
                this.result = result;
            }

            @Override
            public T result() {
                return result;
            }

        }

        private final static class Ignore<T> extends MapResult<T> {

            private final T result;

            private Ignore(T result) {
                this.result = result;
            }

            @Override
            public T result() {
                return result;
            }

        }

        private final static class Failure<T> extends MapResult<T> {

            private final T result;
            private final String debugMessage;

            private Failure(T result, String debugMessage) {
                this.result = result;
                this.debugMessage = debugMessage;
            }

            @Override
            public T result() {
                return result;
            }

            public String getDebugMessage() {
                return debugMessage;
            }

        }

    }

}

