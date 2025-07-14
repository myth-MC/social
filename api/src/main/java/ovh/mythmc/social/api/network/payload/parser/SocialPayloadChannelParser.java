package ovh.mythmc.social.api.network.payload.parser;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.network.exception.UndecodablePayloadException;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;
import ovh.mythmc.social.api.util.registry.RegistryKey;

public final class SocialPayloadChannelParser implements SocialPayloadComponentParser<ChatChannel> {

    SocialPayloadChannelParser() {
    }

    @Override
    public @NotNull ChatChannel parse(final @NotNull SocialPayloadEncoder payload) {
        final var channelRegistry = Social.registries().channels();
        final String channelName = new String(payload.bytes());

        if (!channelRegistry.containsKey(RegistryKey.identified(channelName)))
            throw new UndecodablePayloadException(this, payload.bytes());

        return channelRegistry.value(RegistryKey.identified(channelName)).get();
    }

}
