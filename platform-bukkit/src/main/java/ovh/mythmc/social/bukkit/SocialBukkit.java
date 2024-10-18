package ovh.mythmc.social.bukkit;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.hooks.SocialPluginHook;
import ovh.mythmc.social.api.logger.LoggerWrapper;
import ovh.mythmc.social.bukkit.adventure.BukkitAdventureProvider;
import ovh.mythmc.social.bukkit.commands.impl.GroupCommandImpl;
import ovh.mythmc.social.bukkit.commands.impl.PrivateMessageCommandImpl;
import ovh.mythmc.social.bukkit.commands.impl.ReactionCommandImpl;
import ovh.mythmc.social.bukkit.commands.impl.SocialCommandImpl;
import ovh.mythmc.social.common.boot.SocialBootstrap;
import ovh.mythmc.social.common.listeners.*;
import ovh.mythmc.social.common.reactions.BukkitReactionFactory;

import java.util.Objects;

public final class SocialBukkit extends SocialBootstrap<SocialBukkitPlugin> {

    public static SocialBukkit instance;

    public SocialBukkit(final @NotNull SocialBukkitPlugin plugin) {
        super(plugin, plugin.getDataFolder());

        // Platform implementations
        new BukkitAdventureProvider(plugin);
        new BukkitReactionFactory(plugin);
        instance = this;
    }

    @Override
    public void enable() {
        new Metrics(getPlugin(), 23497);

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
        PluginCommand reaction = getPlugin().getCommand("reaction");
        PluginCommand group = getPlugin().getCommand("group");

        Objects.requireNonNull(social).setExecutor(new SocialCommandImpl());
        Objects.requireNonNull(privateMessage).setExecutor(new PrivateMessageCommandImpl());
        Objects.requireNonNull(reaction).setExecutor(new ReactionCommandImpl());
        Objects.requireNonNull(group).setExecutor(new GroupCommandImpl());
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new SocialPlayerListener(), getPlugin());
        Bukkit.getPluginManager().registerEvents(new SocialBootstrapListener(), getPlugin());

        // External hooks
        for (SocialPluginHook<?> pluginHook : Social.get().getInternalHookManager().getHooks()) {
            if (pluginHook instanceof Listener listener) {
                Bukkit.getPluginManager().registerEvents(listener, getPlugin());
            }
        }
    }

}