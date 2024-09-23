package ovh.mythmc.social.bukkit;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.logger.LoggerWrapper;
import ovh.mythmc.social.bukkit.adventure.BukkitAdventureProvider;
import ovh.mythmc.social.common.boot.SocialBootstrap;
import ovh.mythmc.social.common.listeners.ChatListener;
import ovh.mythmc.social.common.listeners.SocialPlayerListener;
import ovh.mythmc.social.common.listeners.SystemAnnouncementsListener;

public final class SocialBukkit extends SocialBootstrap<SocialBukkitPlugin> {

    public static SocialBukkit instance;

    public SocialBukkit(final @NotNull SocialBukkitPlugin plugin) {
        super(plugin, plugin.getDataFolder());
        new BukkitAdventureProvider(plugin);
        instance = this;
    }

    @Override
    public void enable() {
        registerCommands();
        registerListeners();
    }

    @Override
    public void shutdown() {

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

    private void registerCommands() {

    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new SocialPlayerListener(), getPlugin());

        if (Social.get().getSettings().get().getChat().isEnabled())
            Bukkit.getPluginManager().registerEvents(new ChatListener(), getPlugin());

        if (Social.get().getSettings().get().getSystemMessages().isEnabled())
            Bukkit.getPluginManager().registerEvents(new SystemAnnouncementsListener(), getPlugin());
    }

}