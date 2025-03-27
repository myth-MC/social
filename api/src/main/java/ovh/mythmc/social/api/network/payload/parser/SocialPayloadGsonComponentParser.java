package ovh.mythmc.social.api.network.payload.parser;

import com.google.gson.JsonSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.exception.UndecodablePayloadException;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;

public final class SocialPayloadGsonComponentParser implements SocialPayloadComponentParser<Component> {

    SocialPayloadGsonComponentParser() {
    }

    @Override
    public @NotNull Component parse(final @NotNull SocialPayloadEncoder payload) {
        final String gsonString = new String(payload.bytes());
        Component component;
        try {
            component = GsonComponentSerializer.gson().deserialize(gsonString);
        } catch (JsonSyntaxException e) {
            throw new UndecodablePayloadException(this, payload.bytes());
        }

        return component;
    }

}
