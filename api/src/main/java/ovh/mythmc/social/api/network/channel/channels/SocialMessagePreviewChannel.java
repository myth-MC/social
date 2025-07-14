package ovh.mythmc.social.api.network.channel.channels;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.channel.AbstractNetworkChannelWrapper;
import ovh.mythmc.social.api.network.channel.identifier.NetworkChannelIdentifiers;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;
import ovh.mythmc.social.api.network.payload.parser.SocialPayloadComponentParsers;
import ovh.mythmc.social.api.network.payload.payloads.message.SocialMessagePreviewPayload;

public final class SocialMessagePreviewChannel extends AbstractNetworkChannelWrapper.Bidirectional<SocialMessagePreviewPayload, SocialMessagePreviewPayload> {

    SocialMessagePreviewChannel() {
        super(NetworkChannelIdentifiers.Message.PREVIEW);
    }

    @Override
    public @NotNull SocialMessagePreviewPayload decode(@NotNull SocialPayloadEncoder encoder) {
        final Component adventureComponent = SocialPayloadComponentParsers.component().parse(encoder);
        return new SocialMessagePreviewPayload(adventureComponent);
    }

    @Override
    public @NotNull SocialPayloadEncoder encode(@NotNull SocialMessagePreviewPayload payload) {
        return SocialPayloadEncoder.of(GsonComponentSerializer.gson().serialize(payload.message()));
    }

}
