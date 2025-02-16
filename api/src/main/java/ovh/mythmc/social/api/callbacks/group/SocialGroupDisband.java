package ovh.mythmc.social.api.callbacks.group;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.GroupChatChannel;

@Callback
public final record SocialGroupDisband(GroupChatChannel groupChatChannel) {
    
}
