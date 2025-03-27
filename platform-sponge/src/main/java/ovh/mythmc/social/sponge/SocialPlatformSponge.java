package ovh.mythmc.social.sponge;

import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.sponge.SpongeCommandManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.configuration.SocialConfigProvider;
import ovh.mythmc.social.api.logger.LoggerWrapper;
import ovh.mythmc.social.api.reaction.ReactionFactory;
import ovh.mythmc.social.api.scheduler.SocialScheduler;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.user.SocialUserService;
import ovh.mythmc.social.common.adapter.PlatformAdapter;
import ovh.mythmc.social.common.boot.SocialBootstrap;
import ovh.mythmc.social.sponge.adapter.SpongePlatformAdapter;
import ovh.mythmc.social.sponge.adventure.SpongeAdventureProvider;
import ovh.mythmc.social.sponge.api.SpongeSocialUser;
import ovh.mythmc.social.sponge.api.SpongeSocialUserService;
import ovh.mythmc.social.sponge.scheduler.SpongeSocialSchedulerImpl;

public final class SocialPlatformSponge extends SocialBootstrap {

    private final SocialPlatformSpongePlugin plugin;

    private final SpongeCommandManager<AbstractSocialUser> commandManager;

    public SocialPlatformSponge(final @NotNull SocialPlatformSpongePlugin plugin) {
        super(new SocialConfigProvider(plugin.getConfigDir().toFile()), plugin.getConfigDir().toFile());
        this.plugin = plugin;
        this.commandManager = new SpongeCommandManager<>(
            plugin.getContainer(),
            ExecutionCoordinator.simpleCoordinator(),
            SenderMapper.create(commandCause -> {
                if (commandCause.root() instanceof ServerPlayer player)
                    return Social.get().getUserService().getByUuid(player.uniqueId()).get();

                return AbstractSocialUser.dummy();
            }, user -> ((SpongeSocialUser) user).toCommandCause())
        );

        // Platform implementations
        SocialAdventureProvider.set(new SpongeAdventureProvider());
        SocialScheduler.set(new SpongeSocialSchedulerImpl());

        // Set platform wrapper
        PlatformAdapter.set(new SpongePlatformAdapter());
    }

    @Override
    public Class<? extends AbstractSocialUser> userType() {
        return null;
    }

    @Override
    public void initializeGestalt() {

    }

    @Override
    public void registerPlatformFeatures() {

    }

    @Override
    public void unregisterPlatformFeatures() {

    }

    @Override
    public void enable() {

    }

    @Override
    public org.incendo.cloud.CommandManager<AbstractSocialUser> commandManager() {
        return this.commandManager;
    }

    @Override
    public String version() {
        return plugin.getContainer().metadata().version().getQualifier();
    }

    @Override
    public @NotNull LoggerWrapper getLogger() {
        return new LoggerWrapper() {
            @Override
            public void info(String message, Object... args) {
                plugin.getContainer().logger().info(buildFullMessage(message, args));
            }

            @Override
            public void warn(String message, Object... args) {
                plugin.getContainer().logger().warn(buildFullMessage(message, args));
            }

            @Override
            public void error(String message, Object... args) {
                plugin.getContainer().logger().error(buildFullMessage(message, args));
            }
        };
    }

    @Override
    public @NotNull SocialUserService getUserService() {
        return SpongeSocialUserService.instance;
    }

    @Override
    public @NotNull ReactionFactory getReactionFactory() {
        return null;
    }

}
