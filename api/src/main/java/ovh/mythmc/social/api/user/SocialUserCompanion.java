package ovh.mythmc.social.api.user;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Experimental;

import com.google.common.io.ByteStreams;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.util.CompanionModUtils;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Experimental
public final class SocialUserCompanion {

    public static final Collection<String> S2C_CHANNELS = List.of(
        "social:open",
        "social:close",
        "social:switch",
        "social:closeall",
        "social:mention",
        "social:preview"
    );

    public static final Collection<String> C2S_CHANNELS = List.of(
        "social:bonjour",
        "social:refresh",
        "social:switch",
        "social:preview"
    );

    private final SocialUser user;

    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("social");

    public void open(final @NotNull ChatChannel channel) {
        user.player().ifPresent(player -> {
            String name = channel.getName();
            String alias = CompanionModUtils.getAliasWithPrefix(channel);
            String icon = CompanionModUtils.getIconWithoutBrackets(channel);
            String description = GsonComponentSerializer.gson().serialize(Social.get().getTextProcessor().parse(
                SocialParserContext.builder(user, channel.getHoverText())
                    .channel(channel)
                    .build()));
    
            var bytes = encode(
                name.getBytes(StandardCharsets.UTF_8),
                alias.getBytes(StandardCharsets.UTF_8),
                icon.getBytes(StandardCharsets.UTF_8),
                description.getBytes(StandardCharsets.UTF_8),
                String.valueOf(channel.getColor().value()).getBytes(StandardCharsets.UTF_8)  
            );
    
            player.sendPluginMessage(plugin, "social:open", bytes);
        });
    }

    public void close(final @NotNull ChatChannel channel) {
        user.player().ifPresent(player -> player.sendPluginMessage(plugin, "social:close", encode(channel.getName().getBytes(StandardCharsets.UTF_8))));
    }

    public void clear() {
        user.player().ifPresent(player -> player.sendPluginMessage(plugin, "social:closeall", encode("".getBytes(StandardCharsets.UTF_8))));
    }

    public void mainChannel(final @NotNull ChatChannel channel) {
        user.player().ifPresent(player -> player.sendPluginMessage(plugin, "social:switch", encode(channel.getName().getBytes(StandardCharsets.UTF_8))));
    }

    public void mention(final @NotNull ChatChannel channel, final @NotNull SocialUser sender) {
        user.player().ifPresent(player -> {
            var bytes = encode(
                channel.getName().getBytes(StandardCharsets.UTF_8),
                sender.getCachedDisplayName().getBytes(StandardCharsets.UTF_8),
                sender.getUuid().toString().getBytes(StandardCharsets.UTF_8)
            );
    
            player.sendPluginMessage(plugin, "social:mention", bytes);
        });
    }

    public void preview(final @NotNull Component component) {
        user.player().ifPresent(player -> {
            var bytes = encode(GsonComponentSerializer.gson().serialize(component).getBytes(StandardCharsets.UTF_8));

            player.sendPluginMessage(plugin, "social:preview", bytes);
        });
    }

    public void refresh() {
        Social.get().getChatManager().getChannels().forEach(channel -> {
            if (Social.get().getChatManager().hasPermission(user, channel))
                open(channel);
        });
    }

    private <T> byte[] encode(byte[]... bytes) {
        var out = ByteStreams.newDataOutput();

        for (int i = 0; i < bytes.length; i++) {
            out.write(bytes[i]);
            if (bytes.length > i + 1)
                out.write(";".getBytes(StandardCharsets.UTF_8));
        }

        return out.toByteArray();
    }
    
}
