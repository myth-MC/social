package ovh.mythmc.social.bukkit.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.imdanix.text.MiniTranslator;
import net.kyori.adventure.text.Component;

import java.util.UUID;

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;

/**
 * Hook for PlaceholderAPI integration.
 */
public final class PlaceholderAPIHook implements SocialContextualParser, Listener {

    private final SocialPlaceholderExpansion expansion = new SocialPlaceholderExpansion();

    public @NotNull SocialPlaceholderExpansion getExpansion() {
        return expansion;
    }

    public class SocialPlaceholderExpansion extends PlaceholderExpansion {
        @Override
        public @NotNull String getIdentifier() {
            return "social";
        }

        @Override
        public @NotNull String getAuthor() {
            return "myth-MC";
        }

        @Override
        public @NotNull String getVersion() {
            return Social.get().version();
        }

        @Override
        public String onRequest(OfflinePlayer player, @NotNull String params) {
            final var optionalUser = Social.get().getUserService().getByUuid(player.getUniqueId());
            if (optionalUser.isEmpty())
                return null;

            final var user = optionalUser.get();

            if (params.startsWith("player_")) {
                if (params.equalsIgnoreCase("player_is_in_group")) {
                    return String.valueOf(Social.get().getChatManager().groupChannelByUser(user).isPresent());
                }
                if (params.startsWith("player_is_in_group_")) {
                    final String username = params.substring(params.lastIndexOf("_") + 1);
                    final Player target = Bukkit.getPlayerExact(username);
                    if (target == null)
                        return "false";

                    return String.valueOf(Social.get().getChatManager()
                            .groupChannelByUser(BukkitSocialUser.from(player.getUniqueId())));
                }
            }

            if (params.startsWith("group_")) {
                final var optionalGroupChatChannel = Social.get().getChatManager().groupChannelByUser(user);
                if (optionalGroupChatChannel.isEmpty())
                    return null;

                final var groupChatChannel = optionalGroupChatChannel.get();
                if (params.equalsIgnoreCase("group_name")) {
                    return groupChatChannel.name();
                }
                if (params.equalsIgnoreCase("group_alias")) {
                    return groupChatChannel.alias().get();
                }
                if (params.equalsIgnoreCase("group_alias_or_name")) {
                    return groupChatChannel.aliasOrName();
                }
                if (params.equalsIgnoreCase("group_leader")) {
                    return groupChatChannel.leader().displayNameOrUsername().content();
                }
                if (params.equalsIgnoreCase("group_leader_username")) {
                    return groupChatChannel.leader().username();
                }
                if (params.equalsIgnoreCase("group_leader_uuid")) {
                    return groupChatChannel.leaderUuid().toString();
                }

                if (params.startsWith("group_player_uuid_")) {
                    final Integer integer = tryParse(params.substring(params.lastIndexOf("_") + 1));
                    if (integer == null || integer >= groupChatChannel.members().size())
                        return null;
                    final UUID uuid = groupChatChannel.members().get(integer).uuid();
                    if (uuid == null)
                        return null;

                    return uuid.toString();
                }
                if (params.startsWith("group_player_username_")) {
                    final Integer integer = tryParse(params.substring(params.lastIndexOf("_") + 1));
                    if (integer == null || integer >= groupChatChannel.members().size())
                        return null;

                    final UUID uuid = groupChatChannel.members().get(integer).uuid();
                    if (uuid == null)
                        return null;

                    return Social.get().getUserService().getByUuid(uuid).get().username();
                }
                if (params.startsWith("group_player_")) {
                    final Integer integer = tryParse(params.substring(params.lastIndexOf("_") + 1));
                    if (integer == null || integer >= groupChatChannel.members().size())
                        return null;

                    final UUID uuid = groupChatChannel.members().get(integer).uuid();
                    if (uuid == null)
                        return null;

                    return Social.get().getUserService().getByUuid(uuid).get().displayNameOrUsername().content();
                }
            }

            return null;
        }
    }

    @Override
    public Component parse(SocialParserContext context) {
        final Player player = BukkitSocialUser.from(context.user()).player().orElse(null);
        final String serialized = GsonComponentSerializer.gson().serialize(context.message());

        String parsedMessage = PlaceholderAPI.setPlaceholders(player, serialized);
        parsedMessage = MiniTranslator.toMini(parsedMessage.replace('§', '&'));

        return GsonComponentSerializer.gson().deserialize(parsedMessage);
    }

    private Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}

