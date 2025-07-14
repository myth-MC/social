package ovh.mythmc.social.api.callback.channel;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@Callback
public record SocialChannelPostSwitch(AbstractSocialUser user, boolean informUser, ChatChannel previousChannel, ChatChannel channel) {
    
}
