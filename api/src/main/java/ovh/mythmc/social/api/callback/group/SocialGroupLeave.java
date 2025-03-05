package ovh.mythmc.social.api.callback.group;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@Callback
public final record SocialGroupLeave(GroupChatChannel groupChatChannel, AbstractSocialUser<? extends Object> user) {
    
}
