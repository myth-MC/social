package ovh.mythmc.social.api.callback.group;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Event fired when a {@link SocialUser} leaves a {@link GroupChatChannel}.
 *
 * @param groupChatChannel the {@link GroupChatChannel} the user left
 * @param user             the {@link SocialUser} who left the group
 */
@Callback
public final record SocialGroupLeave(GroupChatChannel groupChatChannel, SocialUser user) {

}
