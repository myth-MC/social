package ovh.mythmc.social.api.network.channel.channels;

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.network.channel.AbstractNetworkChannelWrapper;
import ovh.mythmc.social.api.network.channel.identifier.NetworkChannelIdentifiers;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;
import ovh.mythmc.social.api.network.payload.payloads.channel.SocialChannelOpenPayload;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.util.CompanionModUtils;

public final class SocialChannelOpenChannel extends AbstractNetworkChannelWrapper.S2C<SocialChannelOpenPayload> {

    SocialChannelOpenChannel() {
        super(NetworkChannelIdentifiers.Channel.OPEN);
    }

    @Override
    public @NotNull SocialPayloadEncoder encode(@NotNull SocialChannelOpenPayload payload) {
        final String name = payload.channel().name();
        final String alias = CompanionModUtils.getAliasWithPrefix(payload.channel());
        final String icon = CompanionModUtils.getIconWithoutBrackets(payload.channel());
        final String description = GsonComponentSerializer.gson().serialize(Social.get().getTextProcessor().parse(
            SocialParserContext.builder(AbstractSocialUser.dummy(payload.channel()), payload.channel().description())
                .channel(payload.channel())
                .build()));

        return SocialPayloadEncoder.of(
            name,
            alias,
            icon,
            description,
            String.valueOf(payload.channel().color().value())
        );
    }

}
