package ovh.mythmc.social.api.callback.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.chat.GroupChatChannel;

@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFields({
    @CallbackField(field = "groupChatChannel", getter = "groupChatChannel()"),
    @CallbackField(field = "newAlias", getter = "newAlias()")
})
public final class SocialGroupAliasChange {

    private final GroupChatChannel groupChatChannel;

    private String newAlias;
    
}
