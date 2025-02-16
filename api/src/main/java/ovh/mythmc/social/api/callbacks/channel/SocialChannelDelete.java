package ovh.mythmc.social.api.callbacks.channel;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.ChatChannel;

@Callback
public final record SocialChannelDelete(ChatChannel channel) {
    
}
