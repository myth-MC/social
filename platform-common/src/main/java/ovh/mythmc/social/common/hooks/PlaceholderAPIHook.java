package ovh.mythmc.social.common.hooks;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parsers.SocialContextualParser;
import ovh.mythmc.social.api.users.SocialUser;

@Getter
public final class PlaceholderAPIHook implements SocialContextualParser, Listener {

    private final SocialPlaceholderExpansion expansion = new SocialPlaceholderExpansion();

    @Getter
    public class SocialPlaceholderExpansion extends PlaceholderExpansion {
        private final String identifier = "social";
        private final String author = "myth-MC";
        private final String version = Social.get().version();

        @Override
        public String onRequest(OfflinePlayer player, @NotNull String params) {
            SocialUser user = Social.get().getUserManager().get(player.getUniqueId());
            if (user == null)
                return null;

            if (params.startsWith("player_")) {
                if (params.equalsIgnoreCase("player_is_in_group")) {
                    if (Social.get().getChatManager().getGroupChannelByUser(user) == null)
                        return "false";
                    return "true";
                }
                if (params.startsWith("player_is_in_group_")) {
                    String username = params.substring(params.lastIndexOf("_") + 1);
                    Player target = Bukkit.getPlayerExact(username);
                    if (target == null) 
                        return "false";
                    GroupChatChannel groupChatChannel = Social.get().getChatManager().getGroupChannelByUser(player.getUniqueId());
                    if (groupChatChannel == null)
                        return "false";
                    return "true";
                }
            }

            if (params.startsWith("group_")) {
                GroupChatChannel groupChatChannel = Social.get().getChatManager().getGroupChannelByUser(user);
                if (groupChatChannel == null) return null;

                if (params.equalsIgnoreCase("group_name")) {
                    return groupChatChannel.getName();
                }
                if (params.equalsIgnoreCase("group_alias")) {
                    return groupChatChannel.getAlias();
                }
                if (params.equalsIgnoreCase("group_alias_or_name")) {
                    if (groupChatChannel.getAlias() != null)
                        return groupChatChannel.getAlias();

                    return groupChatChannel.getName();
                }
                if (params.equalsIgnoreCase("group_leader")) {
                    return Social.get().getUserManager().get(groupChatChannel.getLeaderUuid()).getNickname();
                }
                if (params.equalsIgnoreCase("group_leader_username")) {
                    return Social.get().getUserManager().get(groupChatChannel.getLeaderUuid()).getPlayer().getName();
                }
                if (params.equalsIgnoreCase("group_leader_uuid")) {
                    return groupChatChannel.getLeaderUuid().toString();
                }
                
                if (params.startsWith("group_player_uuid_")) {
                    Integer integer = tryParse(params.substring(params.lastIndexOf("_") + 1));
                    if (integer == null || integer >= groupChatChannel.getMembers().size())
                        return null;
                    UUID uuid = groupChatChannel.getMembers().get(integer);
                    if (uuid == null)
                        return null;

                    return uuid.toString();
                }
                if (params.startsWith("group_player_username_")) {
                    Integer integer = tryParse(params.substring(params.lastIndexOf("_") + 1));
                    if (integer == null || integer >= groupChatChannel.getMembers().size())
                        return null;
                    UUID uuid = groupChatChannel.getMembers().get(integer);
                    if (uuid == null)
                        return null;

                    return Social.get().getUserManager().get(uuid).getPlayer().getName();
                }
                if (params.startsWith("group_player_")) {
                    Integer integer = tryParse(params.substring(params.lastIndexOf("_") + 1));
                    if (integer == null || integer >= groupChatChannel.getMembers().size())
                        return null;
                    UUID uuid = groupChatChannel.getMembers().get(integer);
                    if (uuid == null)
                        return null;

                    return Social.get().getUserManager().get(uuid).getNickname();
                }
            }

            return null;
        }
    }

    @Override
    public Component parse(SocialParserContext context) {
        String serialized = LegacyComponentSerializer.legacyAmpersand().serialize(context.message());
        String parsedMessage = PlaceholderAPI.setPlaceholders(context.user().getPlayer(), serialized);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(parsedMessage);
    }

    private Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
