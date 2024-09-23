package ovh.mythmc.social.common.boot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.SocialSupplier;
import ovh.mythmc.social.api.configuration.SocialSettingsProvider;
import ovh.mythmc.social.common.placeholders.impl.ChannelPlaceholderImpl;
import ovh.mythmc.social.common.placeholders.impl.NicknamePlaceholderImpl;

import java.io.File;

@Getter
@RequiredArgsConstructor
public abstract class SocialBootstrap<T> implements Social {

    private T plugin;
    private SocialSettingsProvider settings;

    public SocialBootstrap(final @NotNull T plugin,
                           final File dataDirectory) {
        SocialSupplier.set(this);

        this.plugin = plugin;
        this.settings = new SocialSettingsProvider(dataDirectory);
    }

    public final void initialize() {
        getSettings().load();

        try {
            enable();
        } catch (Throwable throwable) {
            getLogger().error("An error has occurred while initializing social: {}", throwable);
            throwable.printStackTrace(System.err);
            return;
        }

        // update checker

        // register internal placeholders
        Social.get().getPlaceholderProcessor().registerPlaceholder(new NicknamePlaceholderImpl());
        Social.get().getPlaceholderProcessor().registerPlaceholder(new ChannelPlaceholderImpl());
    }

    public abstract void enable();

    public abstract void shutdown();

    public final void reload() {
        getSettings().load();
    }

    public abstract String version();

}
