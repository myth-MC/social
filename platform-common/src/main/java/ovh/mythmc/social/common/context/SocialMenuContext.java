package ovh.mythmc.social.common.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.social.api.context.SocialContext;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Context for a social menu.
 */
public class SocialMenuContext implements SocialContext {

    private final SocialUser viewer;

    private final SocialUser target;

    protected SocialMenuContext(Builder<?> builder) {
        this.viewer = builder.viewer;
        this.target = builder.target;
    }

    public static Builder<?> builder() {
        return new Builder<>();
    }

    public @NotNull SocialUser viewer() {
        return viewer;
    }

    public @Nullable SocialUser target() {
        return target;
    }

    public static class Builder<B extends Builder<B>> {
        protected SocialUser viewer;
        protected SocialUser target;

        protected Builder() {}

        public B viewer(SocialUser viewer) {
            this.viewer = viewer;
            return self();
        }

        public B target(SocialUser target) {
            this.target = target;
            return self();
        }

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }

        public SocialMenuContext build() {
            return new SocialMenuContext(this);
        }
    }

}