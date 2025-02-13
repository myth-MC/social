package ovh.mythmc.social.api.callbacks.group;

import ovh.mythmc.gestalt.callbacks.v1.annotations.Callback;
import ovh.mythmc.social.api.chat.GroupChatChannel;

@Callback
public final record SocialGroupCreate(GroupChatChannel groupChatChannel) {
    
}
