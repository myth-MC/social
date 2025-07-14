package ovh.mythmc.social.api.network.payload.parser;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;

public interface SocialPayloadComponentParser<T> {

    @NotNull T parse(final @NotNull SocialPayloadEncoder payload);

}
