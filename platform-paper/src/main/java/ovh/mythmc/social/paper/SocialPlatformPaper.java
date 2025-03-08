package ovh.mythmc.social.paper;

import ovh.mythmc.social.libs.org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.Gestalt;
import ovh.mythmc.gestalt.loader.PaperGestaltLoader;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.api.bukkit.BukkitSocialUserService;
import ovh.mythmc.social.api.logger.LoggerWrapper;
import ovh.mythmc.social.api.reaction.ReactionFactory;
import ovh.mythmc.social.api.scheduler.SocialScheduler;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.user.SocialUserService;
import ovh.mythmc.social.bukkit.adapter.BukkitPlatformAdapter;
import ovh.mythmc.social.bukkit.feature.hook.DiscordSRVFeature;
import ovh.mythmc.social.bukkit.feature.hook.PlaceholderAPIFeature;
import ovh.mythmc.social.common.adapter.PlatformAdapter;
import ovh.mythmc.social.common.boot.SocialBootstrap;
import ovh.mythmc.social.paper.adventure.PaperAdventureProvider;
import ovh.mythmc.social.paper.callback.game.invoker.AnvilRenameInvoker;
import ovh.mythmc.social.paper.callback.game.invoker.BookEditInvoker;
import ovh.mythmc.social.paper.callback.game.invoker.SignEditInvoker;
import ovh.mythmc.social.paper.callback.game.invoker.UserChatInvoker;
import ovh.mythmc.social.paper.callback.game.invoker.UserDeathInvoker;
import ovh.mythmc.social.paper.callback.game.invoker.UserPresenceInvoker;
import ovh.mythmc.social.paper.feature.listener.PaperChatRendererListener;
import ovh.mythmc.social.paper.reaction.PaperReactionFactory;
import ovh.mythmc.social.paper.scheduler.PaperSocialSchedulerImpl;

public final class SocialPlatformPaper extends SocialBootstrap {
    
    private final SocialPlatformPaperPlugin plugin;

    private final PaperReactionFactory reactionFactory;

    private final LegacyPaperCommandManager<AbstractSocialUser> commandManager;

    private PaperGestaltLoader gestalt;

    public SocialPlatformPaper(final @NotNull SocialPlatformPaperPlugin plugin) {
        super(plugin.getDataFolder());

        this.plugin = plugin;
        this.reactionFactory = new PaperReactionFactory(plugin);
        this.commandManager = new LegacyPaperCommandManager<AbstractSocialUser>(
            plugin, 
            ExecutionCoordinator.simpleCoordinator(),
            SenderMapper.<CommandSender, AbstractSocialUser>create(commandSender -> {
                if (commandSender instanceof Player player)
                    return Social.get().getUserService().getByUuid(player.getUniqueId()).get();

                return AbstractSocialUser.dummy();
            }, user -> {
                if (user instanceof AbstractSocialUser.Dummy<?>)
                    return Bukkit.getConsoleSender();

                return Bukkit.getPlayer(user.uuid());
            }));
            
        // Command manager platform-specific adjustments
        if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            commandManager.registerBrigadier();
        } else if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions();
        }

        // Platform implementations
        SocialAdventureProvider.set(new PaperAdventureProvider());
        SocialScheduler.set(new PaperSocialSchedulerImpl());

        // Set platform wrapper
        PlatformAdapter.set(new BukkitPlatformAdapter());
    
    }

    @Override
    public Class<? extends AbstractSocialUser> userType() {
        return BukkitSocialUser.class;
    }

    @Override
    public void initializeGestalt() {
        gestalt = PaperGestaltLoader.builder()
            .initializer(plugin)
            .build();

        gestalt.initialize();
    }

    @Override
    public void enable() {
        new Metrics(plugin, 23497);

        registerListeners();
    }

    @Override
    public void shutdown() {
        gestalt.terminate();
        super.shutdown();
    }

    @Override
    public String version() {
        return plugin.getPluginMeta().getVersion();
    }

    private void registerListeners() {
        // Invokers
        Bukkit.getPluginManager().registerEvents(new AnvilRenameInvoker(), plugin);
        Bukkit.getPluginManager().registerEvents(new BookEditInvoker(), plugin);
        Bukkit.getPluginManager().registerEvents(new SignEditInvoker(), plugin);
        Bukkit.getPluginManager().registerEvents(new UserChatInvoker(), plugin);
        Bukkit.getPluginManager().registerEvents(new UserDeathInvoker(), plugin);
        Bukkit.getPluginManager().registerEvents(new UserPresenceInvoker(), plugin);
    }

    @Override
    public void registerPlatformFeatures() {
        Gestalt.get().register(
            DiscordSRVFeature.class,
            PlaceholderAPIFeature.class
        );

        Gestalt.get().getListenerRegistry().register(new PaperChatRendererListener());
    }

    @Override
    public void unregisterPlatformFeatures() {
        Gestalt.get().unregister(
            DiscordSRVFeature.class,
            PlaceholderAPIFeature.class
        );
    }

    @Override
    public @NotNull SocialUserService getUserService() {
        return BukkitSocialUserService.instance;
    }

    @Override
    public @NotNull ReactionFactory getReactionFactory() {
        return reactionFactory;
    }

    @Override
    public CommandManager<AbstractSocialUser> commandManager() {
        return commandManager;
    }

    @Override
    public @NotNull LoggerWrapper getLogger() {
        return new LoggerWrapper() {
            @Override
            public void info(String message, Object... args) {
                plugin.getLogger().info(buildFullMessage(message, args));
            }

            @Override
            public void warn(String message, Object... args) {
                plugin.getLogger().warning(buildFullMessage(message, args));
            }

            @Override
            public void error(String message, Object... args) {
                plugin.getLogger().severe(buildFullMessage(message, args));
            }
        };
    }

}
