package ovh.mythmc.social.api.callbacks.channel;

import ovh.mythmc.gestalt.callbacks.v1.annotations.Callback;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

@Callback
public final record SocialChannelPostSwitch(SocialUser user, ChatChannel previousChannel, ChatChannel channel) {
    
}
