package ovh.mythmc.social.api.callback.group;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;

@Callback
public final record SocialGroupDisband(GroupChatChannel groupChatChannel) {
    
}
