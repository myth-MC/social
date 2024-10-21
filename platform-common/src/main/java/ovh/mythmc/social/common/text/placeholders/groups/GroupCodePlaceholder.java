package ovh.mythmc.social.common.text.placeholders.groups;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.parsers.SocialPlaceholder;

public final class GroupCodePlaceholder extends SocialPlaceholder {

    @Override
    public String identifier() {
        return "group_code";
    }

    @Override
    public String process(SocialPlayer player) {
        GroupChatChannel groupChatChannel = Social.get().getChatManager().getGroupChannelByPlayer(player);
        if (groupChatChannel == null)
            return "";

        return "<hover:show_text:'" + Social.get().getConfig().getSettings().getChat().getGroups().getCodeHoverText() + "'>" +
                "<click:copy_to_clipboard:'" + groupChatChannel.getCode() + "'>" + groupChatChannel.getCode() + "</click></hover>";
    }

}
