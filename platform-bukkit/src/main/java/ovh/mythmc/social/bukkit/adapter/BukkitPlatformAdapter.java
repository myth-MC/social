package ovh.mythmc.social.bukkit.adapter;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.ServerLinks;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.api.configuration.section.settings.ServerLinksSettings.ServerLink;
import ovh.mythmc.social.api.network.channel.C2SNetworkChannelWrapper;
import ovh.mythmc.social.api.network.channel.NetworkChannelWrapper;
import ovh.mythmc.social.api.network.channel.S2CNetworkChannelWrapper;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.bukkit.callback.game.invoker.CustomPayloadReceiveInvoker;
import ovh.mythmc.social.common.adapter.PlatformAdapter;

public class BukkitPlatformAdapter extends PlatformAdapter {

    private final @NotNull Plugin plugin = Bukkit.getPluginManager().getPlugin("social");

    private final CustomPayloadReceiveInvoker listener = new CustomPayloadReceiveInvoker();

    // PDC Keys
    private static NamespacedKey DISPLAY_NAME = new NamespacedKey("social", "display-name");
    private static NamespacedKey SOCIAL_SPY = new NamespacedKey("social", "social-spy");

    @Override
    public void registerPayloadChannel(@NotNull NetworkChannelWrapper channel) {
        if (channel instanceof C2SNetworkChannelWrapper<?>) {
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, channel.identifier().toString(),
                    listener);
        }

        if (channel instanceof S2CNetworkChannelWrapper<?>) {
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channel.identifier().toString());
        }
    }

    @Override
    public void sendServerLinks(@NotNull SocialUser user, @NotNull Collection<ServerLink> links) {
        final ServerLinks serverLinks = Bukkit.getServerLinks().copy();
        List.copyOf(serverLinks.getLinks()).forEach(link -> serverLinks.removeLink(link));

        links.forEach(link -> {
            final URI url = URI.create(link.url());

            if (link.displayName() == null) {
                final var type = ServerLinks.Type.valueOf(link.type().name());
                serverLinks.addLink(type, url);
            } else {
                final var displayName = Social.get().getTextProcessor().parse(user, user.mainChannel().get(),
                        Component.text(link.displayName()));
                serverLinks.addLink(LegacyComponentSerializer.legacySection().serialize(displayName), url);
            }
        });

        final var bukkitUser = BukkitSocialUser.from(user);
        bukkitUser.player().ifPresent(player -> {
            player.sendLinks(serverLinks);
        });
    }

    @Override
    public void sendAutoCompletions(@NotNull SocialUser user, @NotNull Collection<String> autoCompletions) {
        final var bukkitUser = BukkitSocialUser.from(user);
        bukkitUser.player().ifPresent(player -> player.addCustomChatCompletions(autoCompletions));
    }

    @Override
    public void sendChatMessage(@NotNull SocialUser user, @NotNull String message) {
        final var bukkitUser = BukkitSocialUser.from(user);
        if (bukkitUser == null)
            return;
        
        bukkitUser.player().ifPresent(player -> player.chat(message));
    }

    @Override
    public void storePreferences(@NotNull SocialUser user) {
        final BukkitSocialUser bukkitUser = BukkitSocialUser.from(user);
        if (bukkitUser == null)
            return;

        
        boolean socialSpy = bukkitUser.socialSpy().get();

        bukkitUser.player().ifPresent(player -> {
            if (bukkitUser.displayName().isPresent()) {
                String serializedDisplayName = MiniMessage.miniMessage().serialize(bukkitUser.displayName().get());
                player.getPersistentDataContainer().set(DISPLAY_NAME, PersistentDataType.STRING, serializedDisplayName);
            }

            player.getPersistentDataContainer().set(SOCIAL_SPY, PersistentDataType.BOOLEAN, socialSpy);
        });
    }

    @Override
    public void restorePreferences(@NotNull SocialUser user) {
        final BukkitSocialUser bukkitUser = BukkitSocialUser.from(user);
        if (bukkitUser == null)
            return;

        bukkitUser.player().ifPresent(player -> {
            PersistentDataContainer pdc = player.getPersistentDataContainer();
            if (pdc.has(DISPLAY_NAME)) {
                String serializedDisplayName = pdc.get(DISPLAY_NAME, PersistentDataType.STRING);
                Component displayName = MiniMessage.miniMessage().deserialize(serializedDisplayName);
                if (displayName instanceof TextComponent textComponent)
                    bukkitUser.displayName().set(textComponent);
            }
            
            if (pdc.has(SOCIAL_SPY)) {
                boolean socialSpy = pdc.get(SOCIAL_SPY, PersistentDataType.BOOLEAN);            
                bukkitUser.socialSpy().set(socialSpy);                
            }
        });
    }

}
