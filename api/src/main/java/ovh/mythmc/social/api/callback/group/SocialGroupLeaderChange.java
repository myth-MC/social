package ovh.mythmc.social.api.callback.group;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Event fired after a {@link GroupChatChannel} changes its leader.
 *
 * @param groupChatChannel the {@link GroupChatChannel} whose leader changed
 * @param previousLeader   the previous {@link SocialUser} who led the group
 * @param leader           the new {@link SocialUser} who now leads the group
 */
@Callback
public final record SocialGroupLeaderChange(GroupChatChannel groupChatChannel, SocialUser previousLeader,
                SocialUser leader) {

}
