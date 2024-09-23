package ovh.mythmc.social.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.logger.LoggerWrapper;
import ovh.mythmc.social.bukkit.adventure.BukkitAdventureProvider;
import ovh.mythmc.social.bukkit.commands.impl.PrivateMessageCommandImpl;
import ovh.mythmc.social.bukkit.commands.impl.SocialCommandImpl;
import ovh.mythmc.social.common.boot.SocialBootstrap;
import ovh.mythmc.social.common.listeners.ChatListener;
import ovh.mythmc.social.common.listeners.SocialPlayerListener;
import ovh.mythmc.social.common.listeners.SystemAnnouncementsListener;
import ovh.mythmc.social.common.placeholders.PAPIExpansion;

import java.util.Objects;

public final class SocialBukkit extends SocialBootstrap<SocialBukkitPlugin> {

    public static SocialBukkit instance;

    public SocialBukkit(final @NotNull SocialBukkitPlugin plugin) {
        super(plugin, plugin.getDataFolder());
        new BukkitAdventureProvider(plugin);
        instance = this;
    }

    @Override
    public void enable() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            new PAPIExpansion();

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
        PluginCommand social = getPlugin().getCommand("social");
        PluginCommand privateMessage = getPlugin().getCommand("pm");

        Objects.requireNonNull(social).setExecutor(new SocialCommandImpl());
        Objects.requireNonNull(privateMessage).setExecutor(new PrivateMessageCommandImpl());
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new SocialPlayerListener(), getPlugin());

        if (Social.get().getConfig().getSettings().getChat().isEnabled())
            Bukkit.getPluginManager().registerEvents(new ChatListener(), getPlugin());

        if (Social.get().getConfig().getSettings().getSystemMessages().isEnabled())
            Bukkit.getPluginManager().registerEvents(new SystemAnnouncementsListener(), getPlugin());
    }

}