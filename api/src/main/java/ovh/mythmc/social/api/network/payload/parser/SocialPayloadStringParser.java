package ovh.mythmc.social.api.network.payload.parser;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;

public final class SocialPayloadStringParser implements SocialPayloadComponentParser<String> {

    SocialPayloadStringParser() {
    }

    @Override
    public @NotNull String parse(final @NotNull SocialPayloadEncoder payload) {
        return new String(payload.bytes());
    }

}
