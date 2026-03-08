package ovh.mythmc.social.api.text.parser;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;

/**
 * The {@code SocialContextualParser} interface defines methods for parsing context information
 * and transforming it into {@link Component} representations. Implementations of this interface provide
 * custom parsing logic based on the context provided.
 * 
 * @see SocialParserContext
 * @see Component
 */
public interface SocialContextualParser {

    /**
     * Parses the given {@link SocialParserContext} and returns a {@link Component} representation.
     * 
     * @param context the context in which the message is being parsed. It contains information such
     *                as the sender, the original message, and other contextual information.
     * @return        a {@link Component} representing the parsed message.
     */
    Component parse(SocialParserContext context);

    /**
     * Checks if this parser supports offline players. The default implementation returns {@code false},
     * but can be overridden by implementations that need to handle offline players.
     * 
     * @return {@code true} if the parser supports offline players, otherwise {@code false}.
     */
    default boolean supportsOfflinePlayers() {
        return false;
    }

    /**
     * Processes a list of parsers in sequence, applying each parser to the given {@link SocialParserContext}.
     * 
     * <p>The context's message is progressively updated by each parser in the list.
     * 
     * @param context          the context to parse the message within.
     * @param requestedParsers the list of parsers to apply in order.
     * @return                 a {@link Component} representing the final parsed message after all parsers 
     *                         have been applied.
     */
    static Component requestList(@NotNull SocialParserContext context, @NotNull List<? extends SocialContextualParser> requestedParsers) {
        var message = context.message();

        for (SocialContextualParser parser : requestedParsers) {
            message = parser.parse(context.withMessage(message));
        }

        return message;
    }

    /**
     * Processes a list of parsers provided by their class types, retrieving the first available parser
     * from the {@link Social} instance's {@link ovh.mythmc.social.api.text.TextProcessor}. 
     * This allows parsing to be performed based on specific parser types passed as class references.
     * 
     * @param context          the context to parse the message within.
     * @param requestedParsers an array of {@link Class} types representing the parsers to request.
     * @return                 a {@link Component} representing the parsed message after applying the 
     *                         selected parsers.
     */
    @SuppressWarnings("unchecked")
    static Component request(@NotNull SocialParserContext context, final @NotNull Class<?>... requestedParsers) {
        return requestList(context, Arrays.stream(requestedParsers)
            .filter(SocialContextualParser.class::isAssignableFrom)
            .map(clazz -> Social.get().getTextProcessor().getContextualParsersByType((Class<SocialContextualParser>) clazz).stream().findFirst().orElse(null))
            .toList());
    }
}