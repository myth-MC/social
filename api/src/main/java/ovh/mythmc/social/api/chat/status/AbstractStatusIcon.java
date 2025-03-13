package ovh.mythmc.social.api.chat.status;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialRendererContext;

import java.util.function.Predicate;

public abstract class AbstractStatusIcon implements StatusIcon {

    static abstract class Builder<T extends Builder<T, R>, R extends AbstractStatusIcon> {

        protected abstract T get();

        public abstract R build();

        protected Component icon;

        protected Predicate<SocialRendererContext> renderIf;

        public T icon(@NotNull Component icon) {
            get().icon = icon;
            return get();
        }

        public T renderIf(@NotNull Predicate<SocialRendererContext> renderIf) {
            get().renderIf = renderIf;
            return get();
        }

    }

    static class Static extends AbstractStatusIcon {

        private final Component icon;

        public static Static from(@NotNull Component icon) {
            return new Static(icon);
        }

        protected Static(@NotNull Component icon) {
            this.icon = icon;
        }

        @Override
        public @NotNull Component icon() {
            return icon;
        }

        @Override
        public @NotNull Predicate<SocialRendererContext> renderIf() {
            return ctx -> true;
        }

    }

}
