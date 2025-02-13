package ovh.mythmc.social.api.callbacks.channel;

import ovh.mythmc.gestalt.callbacks.v1.annotations.Callback;
import ovh.mythmc.social.api.chat.ChatChannel;

@Callback
public final record SocialChannelDelete(ChatChannel channel) {
    
}
