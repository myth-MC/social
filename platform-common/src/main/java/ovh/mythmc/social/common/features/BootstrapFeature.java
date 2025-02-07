package ovh.mythmc.social.common.features;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import ovh.mythmc.gestalt.Gestalt;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.annotations.status.FeatureInitialize;
import ovh.mythmc.gestalt.features.FeatureConstructorParams;
import ovh.mythmc.gestalt.features.GestaltFeature;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.chat.renderer.SocialChatRenderer;
import ovh.mythmc.social.api.chat.renderer.defaults.ConsoleChatRenderer;
import ovh.mythmc.social.api.chat.renderer.defaults.UserChatRenderer;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.features.hooks.DiscordSRVFeature;
import ovh.mythmc.social.common.features.hooks.PlaceholderAPIFeature;

@Feature(group = "social", identifier = "BOOTSTRAP")
public final class BootstrapFeature {

    private boolean initialized = false;

    @FeatureInitialize
    public void initialize() {
        if (initialized)
            return;

        // Register gestalt features
        Gestalt.get().register(
            AnnouncementsFeature.class,
            EmojiFeature.class,
            IPFilterFeature.class,
            TextFormattersFeature.class,
            UpdateCheckerFeature.class,
            URLFilterFeature.class
        );

        // Register features that require a plugin instance
        registerFeatureWithPluginParam(
            AnvilFeature.class,
            BooksFeature.class,
            ChatFeature.class,
            CommandsFeature.class,
            GroupsFeature.class,
            MentionsFeature.class,
            MOTDFeature.class,
            CompanionFeature.class,
            ReactionsFeature.class,
            ServerLinksFeature.class,
            SignsFeature.class,
            SystemMessagesFeature.class,
            DiscordSRVFeature.class,
            PlaceholderAPIFeature.class);

        // Register built-in renderers
        Social.get().getChatManager().registerRenderer(Audience.class, new ConsoleChatRenderer(), options -> {
            return options
                .map(audience -> {
                    var console = SocialAdventureProvider.get().console();
                    if (console.getClass().isInstance(audience))
                        return SocialChatRenderer.MapResult.success(console);

                    return SocialChatRenderer.MapResult.ignore();
                });
        });

        Social.get().getChatManager().registerRenderer(SocialUser.class, new UserChatRenderer(), options -> {
            return options
                .map(audience -> {
                    var uuid = audience.get(Identity.UUID);
                    if (uuid.isEmpty())
                        return SocialChatRenderer.MapResult.ignore();

                    var user = Social.get().getUserManager().getByUuid(uuid.get());
                    if (user == null)
                        return SocialChatRenderer.MapResult.ignore();

                    return SocialChatRenderer.MapResult.success(user);
                });
        });
    }

    @FeatureEnable
    public void enable() {
        Gestalt.get().enableAllFeatures("social");
    }

    @FeatureDisable
    public void disable() {
        Gestalt.get().disableAllFeatures("social");
    }

    private static void registerFeatureWithPluginParam(Class<?>... classes) {
        Arrays.stream(classes).forEach(clazz -> {
            Gestalt.get().register(GestaltFeature.builder()
                .featureClass(clazz)
                .constructorParams(FeatureConstructorParams.builder()
                    .params((JavaPlugin) Bukkit.getPluginManager().getPlugin("social")) // works for now but it needs to be changed asap
                    .types(JavaPlugin.class)
                    .build())
                .build());
        });
    }

}
