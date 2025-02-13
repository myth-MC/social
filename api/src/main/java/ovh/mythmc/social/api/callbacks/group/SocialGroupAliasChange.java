package ovh.mythmc.social.api.callbacks.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.gestalt.callbacks.v1.annotations.Callback;
import ovh.mythmc.gestalt.callbacks.v1.annotations.CallbackFieldGetter;
import ovh.mythmc.social.api.chat.GroupChatChannel;

@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
public final class SocialGroupAliasChange {

    @CallbackFieldGetter("groupChatChannel")
    private final GroupChatChannel groupChatChannel;

    @CallbackFieldGetter("newAlias")
    private String newAlias;
    
}
