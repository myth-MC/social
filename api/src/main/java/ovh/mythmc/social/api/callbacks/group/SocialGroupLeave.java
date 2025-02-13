package ovh.mythmc.social.api.callbacks.group;

import ovh.mythmc.gestalt.callbacks.v1.annotations.Callback;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

@Callback
public final record SocialGroupLeave(GroupChatChannel groupChatChannel, SocialUser user) {
    
}
