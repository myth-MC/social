package ovh.mythmc.social.api.callback.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.chat.ChatChannel;

@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFields({
    @CallbackField(field = "channel", getter = "channel()"),
    @CallbackField(field = "newAlias", getter = "newAlias()")
})
public final class SocialChannelAliasChange {

    private final ChatChannel channel;

    private String newAlias;
    
}
