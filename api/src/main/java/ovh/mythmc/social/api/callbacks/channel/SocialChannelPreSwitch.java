package ovh.mythmc.social.api.callbacks.channel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.gestalt.callbacks.v1.annotations.Callback;
import ovh.mythmc.gestalt.callbacks.v1.annotations.CallbackFieldGetter;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
public final class SocialChannelPreSwitch {

    @CallbackFieldGetter("user")
    private @NotNull SocialUser user;

    @CallbackFieldGetter("channel")
    private @NotNull ChatChannel channel;

    @CallbackFieldGetter("cancelled")
    private boolean cancelled = false;
    
}
