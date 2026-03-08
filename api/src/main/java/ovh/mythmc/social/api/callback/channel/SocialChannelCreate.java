package ovh.mythmc.social.api.callback.channel;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.channel.ChatChannel;

/**
 * Event fired when a {@link ChatChannel} is created.
 *
 * @param channel the {@link ChatChannel} that was created
 */
@Callback
public record SocialChannelCreate(ChatChannel channel) {
    
}
