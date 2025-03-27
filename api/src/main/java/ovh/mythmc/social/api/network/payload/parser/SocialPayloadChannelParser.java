package ovh.mythmc.social.api.network.payload.parser;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.ChatManager;
import ovh.mythmc.social.api.network.exception.UndecodablePayloadException;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;

public final class SocialPayloadChannelParser implements SocialPayloadComponentParser<ChatChannel> {

    SocialPayloadChannelParser() {
    }

    @Override
    public @NotNull ChatChannel parse(final @NotNull SocialPayloadEncoder payload) {
        final ChatManager chatManager = Social.get().getChatManager();
        final String channelName = new String(payload.bytes());

        if (!chatManager.exists(channelName))
            throw new UndecodablePayloadException(this, payload.bytes());

        return chatManager.getChannel(channelName);
    }

}
