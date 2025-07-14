package ovh.mythmc.social.api.network.exception;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.payload.parser.SocialPayloadComponentParser;

public class UndecodablePayloadException extends IllegalArgumentException {

    public UndecodablePayloadException(final @NotNull SocialPayloadComponentParser<?> wrapper, final byte[] payload) {
        super("Parser " + wrapper.getClass().getSimpleName() + " could not decode payload " + new String(payload));
    }

}
