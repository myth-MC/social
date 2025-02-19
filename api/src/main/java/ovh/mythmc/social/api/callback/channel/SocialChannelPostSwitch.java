package ovh.mythmc.social.api.callback.channel;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

@Callback
public final record SocialChannelPostSwitch(SocialUser user, ChatChannel previousChannel, ChatChannel channel) {
    
}
