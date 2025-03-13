package ovh.mythmc.social.api.callback.channel;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.ChatChannel;

@Callback
public record SocialChannelDelete(ChatChannel channel) {
    
}
