package ovh.mythmc.social.api.callbacks.channel;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

@Callback
public final record SocialChannelPostSwitch(SocialUser user, ChatChannel previousChannel, ChatChannel channel) {
    
}
