package ovh.mythmc.social.api.network.payload.parser;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;

public final class SocialPayloadTextComponentParser implements SocialPayloadComponentParser<TextComponent> {

    SocialPayloadTextComponentParser() {
    }

    @Override
    public @NotNull TextComponent parse(final @NotNull SocialPayloadEncoder payload) {
        return Component.text(new String(payload.bytes()));
    }

}
