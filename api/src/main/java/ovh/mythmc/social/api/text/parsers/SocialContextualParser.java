package ovh.mythmc.social.api.text.parsers;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;

public interface SocialContextualParser {
    
    Component parse(SocialParserContext context);

    default Component request(@NotNull SocialParserContext context, @NotNull List<SocialContextualParser> requestedParsers) {
        Component message = context.message();

        List<SocialContextualParser> parsers = Social.get().getTextProcessor().getContextualParsersWithGroupMembers().stream().filter(p -> requestedParsers.contains(p)).toList();
        for (SocialContextualParser parser : parsers) {
            message = parser.parse(context.withMessage(message));
        }

        return message;
    }

    default Component request(@NotNull SocialParserContext context, final @NotNull Class<?>... requestedParsers) {
        return request(context, Arrays.stream(requestedParsers).map(clazz -> Social.get().getTextProcessor().getContextualParserByClass(clazz)).toList());
    }

}
