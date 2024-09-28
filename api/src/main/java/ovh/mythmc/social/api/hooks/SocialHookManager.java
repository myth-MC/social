package ovh.mythmc.social.api.hooks;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Getter
public final class SocialHookManager {

    public static final SocialHookManager instance = new SocialHookManager();

    private final Collection<SocialPluginHook<?>> hooks = new ArrayList<>();

    public void registerHooks(SocialPluginHook<?>... hooks) {
        Arrays.stream(hooks).forEach(hook -> {
            Social.get().getLogger().info("Registered a new plugin hook: " + hook.identifier());
            this.hooks.add(hook);
        });
    }

    public void unregisterHooks(SocialPluginHook<?>... hooks) {
        Arrays.stream(hooks).forEach(hook -> {
            Social.get().getLogger().info("Unregistering plugin hook '" + hook.identifier() + "'");
        });
    }

    public SocialPluginHook<?> getByIdentifier(final @NotNull String identifier) {
        for (SocialPluginHook<?> hook : this.hooks) {
            if (hook.identifier().equals(identifier))
                return hook;
        }

        return null;
    }

    public boolean isEnabled(final @NotNull String identifier) {
        SocialPluginHook<?> hook = getByIdentifier(identifier);
        if (hook != null)
            return hook.isEnabled();

        return false;
    }

}
