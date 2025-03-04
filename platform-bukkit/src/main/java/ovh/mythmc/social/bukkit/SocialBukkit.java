package ovh.mythmc.social.bukkit;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.loader.BukkitGestaltLoader;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.logger.LoggerWrapper;
import ovh.mythmc.social.api.reaction.ReactionFactory;
import ovh.mythmc.social.bukkit.adapter.BukkitChatEventAdapter;
import ovh.mythmc.social.bukkit.adapter.BukkitPlatformAdapter;
import ovh.mythmc.social.bukkit.adventure.BukkitAdventureProvider;
import ovh.mythmc.social.bukkit.reaction.BukkitReactionFactory;
import ovh.mythmc.social.common.adapter.ChatEventAdapter;
import ovh.mythmc.social.common.adapter.PlatformAdapter;
import ovh.mythmc.social.common.boot.SocialBootstrap;
import ovh.mythmc.social.common.listener.*;

public final class SocialBukkit extends SocialBootstrap<SocialBukkitPlugin> {

    public static SocialBukkit instance;

    private BukkitGestaltLoader gestalt;

    public SocialBukkit(final @NotNull SocialBukkitPlugin plugin) {
        super(plugin, plugin.getDataFolder());

        // Platform implementations
        SocialAdventureProvider.set(new BukkitAdventureProvider(plugin));
        ReactionFactory.set(new BukkitReactionFactory(plugin));

        // Set platform wrapper
        PlatformAdapter.set(new BukkitPlatformAdapter());

        // Set chat wrapper
        ChatEventAdapter.set(new BukkitChatEventAdapter());

        instance = this;
    }

    @Override
    public void initializeGestalt() {
        gestalt = BukkitGestaltLoader.builder()
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
        super.shutdown();
    }

    @Override
    public String version() {
        return getPlugin().getDescription().getVersion();
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
    }

}