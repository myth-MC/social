package ovh.mythmc.social.api.callback.group;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;

/**
 * Event fired when a {@link GroupChatChannel} is created.
 *
 * @param groupChatChannel the {@link GroupChatChannel} that was created
 */
@Callback
public final record SocialGroupCreate(GroupChatChannel groupChatChannel) {
    
}
