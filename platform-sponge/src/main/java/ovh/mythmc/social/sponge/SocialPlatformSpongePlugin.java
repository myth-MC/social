package ovh.mythmc.social.sponge;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Server;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.nio.file.Path;

/**
 * Sponge plugin implementation for Social.
 */
@Plugin("social")
public final class SocialPlatformSpongePlugin {

    private final PluginContainer container;

    private final Logger logger;

    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    SocialPlatformSpongePlugin(final PluginContainer container, final Logger logger) {
        this.container = container;
        this.logger = logger;
    }

    public @NotNull PluginContainer getContainer() {
        return container;
    }

    public @NotNull Logger getLogger() {
        return logger;
    }

    public @Nullable Path getConfigDir() {
        return configDir;
    }

    @Listener
    public void onServerStart(final StartedEngineEvent<Server> event) {

    }

    @Listener
    public void onServerStop(final StoppingEngineEvent<Server> event) {

    }

}

