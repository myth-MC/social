package ovh.mythmc.social.common.boot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.SocialSupplier;
import ovh.mythmc.social.api.configuration.SocialConfigProvider;
import ovh.mythmc.social.common.placeholders.impl.ChannelPlaceholderImpl;
import ovh.mythmc.social.common.placeholders.impl.NicknamePlaceholderImpl;
import ovh.mythmc.social.common.placeholders.impl.UsernamePlaceholderImpl;

import java.io.File;

@Getter
@RequiredArgsConstructor
public abstract class SocialBootstrap<T> implements Social {

    private T plugin;
    private SocialConfigProvider config;

    public SocialBootstrap(final @NotNull T plugin,
                           final File dataDirectory) {
        SocialSupplier.set(this);

        this.plugin = plugin;
        this.config = new SocialConfigProvider(dataDirectory);
    }

    public final void initialize() {
        getConfig().load();

        try {
            enable();
        } catch (Throwable throwable) {
            getLogger().error("An error has occurred while initializing social: {}", throwable);
            throwable.printStackTrace(System.err);
            return;
        }

        // update checker

        // register internal placeholders
        Social.get().getTextProcessor().registerParser(
                new NicknamePlaceholderImpl(),
                new ChannelPlaceholderImpl(),
                new UsernamePlaceholderImpl()
        );
    }

    public abstract void enable();

    public abstract void shutdown();

    public final void reload() {
        getConfig().load();
    }

    public abstract String version();

}
