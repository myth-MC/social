package ovh.mythmc.social.api.callback.channel;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@Callback
public final record SocialChannelPostSwitch(AbstractSocialUser<? extends Object> user, ChatChannel previousChannel, ChatChannel channel) {
    
}
