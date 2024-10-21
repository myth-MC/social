package ovh.mythmc.social.common.text.placeholders.chat;

import ovh.mythmc.social.api.text.parsers.SocialPlaceholder;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class ChannelPlaceholder extends SocialPlaceholder {

    @Override
    public String identifier() {
        return "channel";
    }

    @Override
    public String process(SocialPlayer player) {
        String channelName = player.getMainChannel().getName();
        if (player.getMainChannel() instanceof GroupChatChannel groupChatChannel && groupChatChannel.getAlias() != null)
            channelName = groupChatChannel.getAlias();
        String hexColor = "<color:" + player.getMainChannel().getColor().asHexString().toUpperCase() + ">";
        return hexColor + channelName + "</color>";
    }

}
