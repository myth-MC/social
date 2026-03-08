package ovh.mythmc.social.api.callback.group;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Event fired when a {@link SocialUser} joins a {@link GroupChatChannel}.
 *
 * @param groupChatChannel the {@link GroupChatChannel} the user who joined
 * @param user             the {@link SocialUser} who joined the group
 */
@Callback
public final record SocialGroupJoin(GroupChatChannel groupChatChannel, SocialUser user) {

}
