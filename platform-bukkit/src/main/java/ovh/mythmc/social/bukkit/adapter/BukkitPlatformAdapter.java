package ovh.mythmc.social.bukkit.adapter;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ServerLinks;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.api.configuration.section.settings.ServerLinksSettings.ServerLink;
import ovh.mythmc.social.bukkit.callback.game.invoker.CustomPayloadReceiveInvoker;
import ovh.mythmc.social.common.adapter.PlatformAdapter;

public final class BukkitPlatformAdapter extends PlatformAdapter<BukkitSocialUser> {

    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("social");

    private final CustomPayloadReceiveInvoker listener = new CustomPayloadReceiveInvoker();

    @Override
    public void registerS2CPayloadChannel(@NotNull String channel) {
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channel);
    }

    @Override
    public void registerC2SPayloadChannel(@NotNull String channel) {
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, channel, listener);
    }

    @Override
    public void sendServerLinks(@NotNull BukkitSocialUser user, @NotNull Collection<ServerLink> links) {
        final ServerLinks serverLinks = Bukkit.getServerLinks().copy();
        List.copyOf(serverLinks.getLinks()).forEach(link -> serverLinks.removeLink(link));

        links.forEach(link -> {
            final URI url = URI.create(link.url());

            if (link.displayName() == null) {
                final var type = ServerLinks.Type.valueOf(link.type().name());
                serverLinks.addLink(type, url);
            } else {
                final var displayName = Social.get().getTextProcessor().parse(user, user.mainChannel(), Component.text(link.displayName()));
                serverLinks.addLink(LegacyComponentSerializer.legacySection().serialize(displayName), url);
            }
        });

        user.player().ifPresent(player -> {
            player.sendLinks(serverLinks);
        });
    }

    @Override
    public void sendAutoCompletions(@NotNull BukkitSocialUser user, @NotNull Collection<String> autoCompletions) {
        user.player().ifPresent(player -> player.addCustomChatCompletions(autoCompletions));
    }
    
}
