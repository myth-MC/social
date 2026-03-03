package ovh.mythmc.social.api.callback.channel;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Event fired <em>after</em> a {@link SocialUser} switches their main {@link ChatChannel}.
 * 
 * <p>For the cancellable event fired <em>before</em> the user switches, see
 * {@link SocialChannelPreSwitch}.
 *
 * @param user            the {@link SocialUser} that switched their main channel
 * @param previousChannel the user's previous main {@link ChatChannel}
 * @param channel         the user's new main {@link ChatChannel}
 */
@Callback
public record SocialChannelPostSwitch(SocialUser user, ChatChannel previousChannel,
        ChatChannel channel) {

}
