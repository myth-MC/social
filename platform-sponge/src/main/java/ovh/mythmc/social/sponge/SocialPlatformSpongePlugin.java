package ovh.mythmc.social.sponge;

import com.google.inject.Inject;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.nio.file.Path;

@Plugin("social")
@Getter
public final class SocialPlatformSpongePlugin {

    private final PluginContainer container;

    private final Logger logger;

    @Inject
    SocialPlatformSpongePlugin(final PluginContainer container, final Logger logger) {
        this.container = container;
        this.logger = logger;
    }

    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Listener
    public void onServerStart(final StartedEngineEvent<Server> event) {

    }

    @Listener
    public void onServerStop(final StoppingEngineEvent<Server> event) {

    }

}
