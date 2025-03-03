package ovh.mythmc.social.api.text.parser;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;

public interface SocialContextualParser {
    
    Component parse(SocialParserContext context);

    default boolean supportsOfflinePlayers() { return false; }

    static Component requestList(@NotNull SocialParserContext context, @NotNull List<? extends SocialContextualParser> requestedParsers) {
        var message = context.message();

        for (SocialContextualParser parser : requestedParsers) {
            message = parser.parse(context.withMessage(message));
        }

        return message;
    }

    @SuppressWarnings("unchecked")
    static Component request(@NotNull SocialParserContext context, final @NotNull Class<?>... requestedParsers) {
        return requestList(context, Arrays.stream(requestedParsers)
            .filter(clazz -> SocialContextualParser.class.isAssignableFrom(clazz))
            .map(clazz -> Social.get().getTextProcessor().getContextualParsersByType((Class<SocialContextualParser>) clazz).stream().findFirst().orElse(null))
            .toList());
    }

}
