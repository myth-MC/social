package ovh.mythmc.social.api.callback.channel;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.channel.ChatChannel;

/**
 * Event fired when a {@link ChatChannel} is deleted.
 *
 * @param channel the {@link ChatChannel} that was deleted
 */
@Callback
public record SocialChannelDelete(ChatChannel channel) {
    
}
