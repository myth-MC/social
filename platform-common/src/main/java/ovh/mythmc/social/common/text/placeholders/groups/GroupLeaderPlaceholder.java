package ovh.mythmc.social.common.text.placeholders.groups;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.parsers.SocialPlaceholder;

public final class GroupLeaderPlaceholder extends SocialPlaceholder {

    @Override
    public boolean legacySupport() {
        return true;
    }

    @Override
    public String identifier() {
        return "group_leader";
    }

    @Override
    public String process(SocialPlayer player) {
        GroupChatChannel groupChatChannel = Social.get().getChatManager().getGroupChannelByPlayer(player);
        if (groupChatChannel == null)
            return "";

        return ChatColor.stripColor(Bukkit.getPlayer(groupChatChannel.getLeaderUuid()).getDisplayName());
    }

}
