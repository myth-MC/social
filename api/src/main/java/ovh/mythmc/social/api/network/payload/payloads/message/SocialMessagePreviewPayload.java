package ovh.mythmc.social.api.network.payload.payloads.message;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.payload.AbstractNetworkPayloadWrapper;

public final class SocialMessagePreviewPayload extends AbstractNetworkPayloadWrapper.Bidirectional {

    private final Component message;

    public SocialMessagePreviewPayload(final @NotNull Component message) {
        this.message = message;
    }

    public Component message() {
        return this.message;
    }

}
