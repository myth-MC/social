package ovh.mythmc.social.api.callback.group;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@Callback
public final record SocialGroupJoin(GroupChatChannel groupChatChannel, AbstractSocialUser user) {
    
}
