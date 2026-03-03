package ovh.mythmc.social.api.text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.text.parser.SocialIdentifiedParser;

/**
 * A configurable text processor that applies a specific parser list to a
 * {@link ovh.mythmc.social.api.context.SocialParserContext}.
 *
 * <p>
 * Use the {@link #builder() } to create instances, or call
 * {@link #defaultProcessor()} for a processor that mirrors the global parser
 * configuration.
 * Parsers can be excluded individually via the {@code exclusions} builder
 * field
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@Data
@With
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
public class TextProcessor {

    @Builder.Default
    private final List<SocialContextualParser> parsers = new ArrayList<>();

    @Builder.Default
    private final Set<Class<? extends SocialContextualParser>> exclusions = new HashSet<>();

    @Builder.Default
    private boolean restrictToPlayerInputParsers = false;

    public static TextProcessorBuilder builder() {
        return new TextProcessorBuilder();
    }

    /**
     * Creates a processor that uses the same parser list as
     * {@link ovh.mythmc.social.api.text.GlobalTextProcessor#getContextualParsers()}.
     *
     * @return a default processor
     */
    public static TextProcessor defaultProcessor() {
        return TextProcessor.builder()
                .parsers(Social.get().getTextProcessor().getContextualParsers())
                .build();
    }

    /**
     * Runs all parsers in order against the given context and returns the final
     * component.
     *
     * <p>
     * Each parser may also transform hover-event text. An individual parser is
     * skipped if:
     * <ul>
     * <li>It is a {@link ovh.mythmc.social.api.text.filter.SocialFilterLike} and
     * player-input mode is off</li>
     * <li>It is not a
     * {@link ovh.mythmc.social.api.text.parser.SocialUserInputParser} and
     * player-input mode is on</li>
     * <li>The user is offline and the parser does not support offline players</li>
     * <li>The parser has been invoked more than 3 times (a warning is logged and
     * parsing stops)</li>
     * </ul>
     *
     * @param context the parse context
     * @return the fully parsed component
     */
    public Component parse(@NotNull SocialParserContext context) {
        ParseExecution execution = new ParseExecution(this, context, 5000);
        return execution.run();
    }

    /**
     * Returns all parsers in this processor's list (including nested group members)
     * that
     * are instances of the given type.
     *
     * @param type the parser type to search for
     * @param <T>  the type parameter
     * @return a list of matching parsers
     */
    @SuppressWarnings("unchecked")
    public <T extends SocialContextualParser> List<T> getContextualParsersByType(@NotNull Class<T> type) {
        final List<T> result = new ArrayList<>();
        for (SocialContextualParser parser : parsers) {
            if (type.isInstance(parser)) {
                result.add((T) parser);
            } else if (parser instanceof SocialParserGroup group) {
                result.addAll(group.getByType(type));
            }
        }
        return List.copyOf(result);
    }

    /**
     * Returns the {@link SocialParserGroup} that contains the given parser class,
     * if any.
     *
     * @param parserClass the parser class to search for
     * @return an optional containing the group
     */
    public Optional<SocialParserGroup> getGroupByContextualParser(
            @NotNull Class<? extends SocialContextualParser> parserClass) {
        return getContextualParsersByType(SocialParserGroup.class).stream()
                .filter(group -> !group.getByType(parserClass).isEmpty())
                .findFirst();
    }

    /**
     * Returns the parser of the given type whose
     * {@link SocialIdentifiedParser#identifier()}
     * matches the given string, if any.
     *
     * @param type       the parser type
     * @param identifier the identifier to match
     * @param <T>        the type parameter
     * @return an optional containing the matching parser
     */
    public <T extends SocialIdentifiedParser> Optional<T> getIdentifiedContextualParser(@NotNull Class<T> type,
            @NotNull String identifier) {
        return getContextualParsersByType(type).stream()
                .filter(parser -> parser.identifier().equals(identifier))
                .findFirst();
    }

    List<SocialContextualParser> getWithExclusions() { // todo: move to ParseExecution?
        if (exclusions.isEmpty())
            return List.copyOf(parsers);
        return parsers.stream()
                .filter(parser -> !exclusions.contains(parser.getClass()))
                .toList();
    }

    public static class TextProcessorBuilder {

    }

}
