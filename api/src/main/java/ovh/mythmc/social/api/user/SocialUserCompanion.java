package ovh.mythmc.social.api.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Experimental;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.network.channel.channels.SocialPayloadChannels;
import ovh.mythmc.social.api.network.payload.payloads.channel.*;
import ovh.mythmc.social.api.network.payload.payloads.message.SocialMessagePreviewPayload;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Experimental
public final class SocialUserCompanion {

    private final AbstractSocialUser user;

    public void open(final @NotNull ChatChannel channel) {
        user.sendCustomPayload(SocialPayloadChannels.OPEN_CHANNEL, new SocialChannelOpenPayload(channel));
    }

    public void close(final @NotNull ChatChannel channel) {
        user.sendCustomPayload(SocialPayloadChannels.CLOSE_CHANNEL, new SocialChannelClosePayload(channel));
    }

    public void clear() {
        user.sendCustomPayload(SocialPayloadChannels.CLOSE_ALL_CHANNELS, new SocialChannelCloseAllPayload());
    }

    public void mainChannel(final @NotNull ChatChannel channel) {
        user.sendCustomPayload(SocialPayloadChannels.SWITCH_CHANNEL, new SocialChannelSwitchPayload(channel));
    }

    public void mention(final @NotNull ChatChannel channel, final @NotNull AbstractSocialUser sender) {
        user.sendCustomPayload(SocialPayloadChannels.MENTION, new SocialChannelMentionPayload(channel, sender));
    }

    public void preview(final @NotNull Component component) {
        user.sendCustomPayload(SocialPayloadChannels.MESSAGE_PREVIEW, new SocialMessagePreviewPayload(component));
    }

    public void refresh() {
        Social.registries().channels().values().forEach(channel -> {
            if (Social.get().getChatManager().hasPermission(user, channel))
                open(channel);
        });
    }
    
}
