package ovh.mythmc.social.paper;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.loader.PaperGestaltLoader;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.logger.LoggerWrapper;
import ovh.mythmc.social.api.reactions.ReactionFactory;
import ovh.mythmc.social.common.boot.SocialBootstrap;
import ovh.mythmc.social.common.listeners.SocialUserListener;
import ovh.mythmc.social.common.wrappers.ChatEventWrapper;
import ovh.mythmc.social.common.wrappers.PlatformWrapper;
import ovh.mythmc.social.paper.adventure.PaperAdventureProvider;
import ovh.mythmc.social.paper.reactions.PaperReactionFactory;
import ovh.mythmc.social.paper.wrappers.PaperChatEventWrapper;
import ovh.mythmc.social.paper.wrappers.PaperPlatformWrapper;

public final class SocialPaper extends SocialBootstrap<SocialPaperPlugin> {
    
    public static SocialPaper instance;

    private PaperGestaltLoader gestalt;

    public SocialPaper(final @NotNull SocialPaperPlugin plugin) {
        super(plugin, plugin.getDataFolder());

        ReactionFactory.set(new PaperReactionFactory(plugin));
        SocialAdventureProvider.set(new PaperAdventureProvider());

        // Set platform wrapper
        PlatformWrapper.set(new PaperPlatformWrapper());

        // Set chat wrapper
        ChatEventWrapper.set(new PaperChatEventWrapper());
        
        instance = this;
    }

    @Override
    public void initializeGestalt() {
        gestalt = PaperGestaltLoader.builder()
            .initializer(getPlugin())
            .build();

        gestalt.initialize();
    }

    @Override
    public void enable() {
        new Metrics(getPlugin(), 23497);

        registerListeners();
    }

    @Override
    public void shutdown() {
        gestalt.terminate();
    }

    @Override
    public String version() {
        return getPlugin().getPluginMeta().getVersion();
    }

    @Override
    public @NotNull LoggerWrapper getLogger() {
        return new LoggerWrapper() {
            @Override
            public void info(String message, Object... args) {
                getPlugin().getLogger().info(buildFullMessage(message, args));
            }

            @Override
            public void warn(String message, Object... args) {
                getPlugin().getLogger().warning(buildFullMessage(message, args));
            }

            @Override
            public void error(String message, Object... args) {
                getPlugin().getLogger().severe(buildFullMessage(message, args));
            }
        };
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new SocialUserListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new PaperChatEventWrapper(), getPlugin());
    }

}
