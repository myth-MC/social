package ovh.mythmc.social.api.callbacks.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackFieldGetter;
import ovh.mythmc.callbacks.annotations.v1.CallbackFieldGetters;
import ovh.mythmc.social.api.chat.GroupChatChannel;

@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFieldGetters({
    @CallbackFieldGetter(field = "groupChatChannel", getter = "groupChatChannel()"),
    @CallbackFieldGetter(field = "newAlias", getter = "newAlias()")
})
public final class SocialGroupAliasChange {

    private final GroupChatChannel groupChatChannel;

    private String newAlias;
    
}
