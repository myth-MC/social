package ovh.mythmc.social.api.chat.renderer;

import java.util.function.Function;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.context.SocialRendererContext;

public interface SocialChatRenderer<T> {

    static <T> SocialChatRenderer.Builder<T> builder(@NotNull SocialChatRenderer<T> renderer) { return new Builder<T>(renderer); }

    SocialRendererContext render(@NotNull T target, @NotNull SocialRegisteredMessageContext context);

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Builder<T> {

        private final SocialChatRenderer<T> renderer;

        private Predicate<SocialRegisteredMessageContext> renderIf;

        private Function<Audience, MapResult<T>> map;

        public Builder<T> renderIf(Predicate<SocialRegisteredMessageContext> renderIf) {
            this.renderIf = renderIf;
            return this;
        }

        public Builder<T> map(Function<Audience, MapResult<T>> map) {
            this.map = map;
            return this;
        }

        public SocialChatRenderer.Registered<T> build() {
            return new Registered<T>(renderer, renderIf, map);
        }

    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Registered<T> {

        private final SocialChatRenderer<T> renderer;

        private final Predicate<SocialRegisteredMessageContext> renderIf;

        private final Function<Audience, MapResult<T>> map;

        public MapResult<T> mapFromAudience(@NotNull Audience audience) {
            if (map == null)
                return MapResult.failure("This renderer cannot map objects!");

            var result = map.apply(audience);
            return result;
        }

        public SocialRendererContext render(@NotNull Audience audience, @NotNull SocialRegisteredMessageContext context) {
            var result = mapFromAudience(audience);
            if (result.isIgnored())
                return null;

            if (result instanceof MapResult.Failure failure) {
                Social.get().getLogger().error(failure.debugMessage);

                return null;
            }

            return render(result.result(), context);
        }

        public SocialRendererContext render(@NotNull T target, @NotNull SocialRegisteredMessageContext context) {
            if (renderIf != null && !renderIf.test(context))
                return null;

            return renderer.render(target, context);
        }

    }

    @NonExtendable
    static abstract class MapResult<T> {

        public abstract T result();

        public static <T> MapResult.Success<T> success(T result) {
            return new Success<>(result);
        }

        public static <T> MapResult.Ignore<T> ignore() {
            return new Ignore<T>(null);
        }

        public static <T> MapResult.Failure<T> failure(String debugMessage) {
            return new Failure<>(null, debugMessage);
        }

        public boolean isSuccess() { return this instanceof MapResult.Success; }

        public boolean isIgnored() { return this instanceof MapResult.Ignore; }

        public boolean isFailure() { return this instanceof MapResult.Failure; }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        @Getter
        @Accessors(fluent = true)
        private final static class Success<T> extends MapResult<T> {

            private final T result;

        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        @Getter
        @Accessors(fluent = true)
        private final static class Ignore<T> extends MapResult<T> {

            private final T result;

        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        @Getter
        @Accessors(fluent = true)
        private final static class Failure<T> extends MapResult<T> {

            private final T result;

            private final String debugMessage;

        }

    }

}
