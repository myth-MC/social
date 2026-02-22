package ovh.mythmc.social.api.text;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.Action;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialProcessorContext;
import ovh.mythmc.social.api.text.filter.SocialFilterLike;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.text.parser.SocialIdentifiedParser;
import ovh.mythmc.social.api.text.parser.SocialUserInputParser;

/**
 * A configurable text processor that applies a specific parser list to a
 * {@link ovh.mythmc.social.api.context.SocialParserContext}.
 *
 * <p>
 * Use the {@link #builder()} to create instances, or call
 * {@link #defaultProcessor()} for a processor that mirrors the global parser
 * configuration.
 * Parsers can be excluded individually via the {@code exclusions} builder
 * field, and extra
 * parsers can be injected mid-parse via
 * {@link #injectParser(SocialContextualParser)}.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@Data
@With
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
public class CustomTextProcessor {

    @Builder.Default
    private final List<SocialContextualParser> parsers = new ArrayList<>();

    @Builder.Default
    private final Set<Class<? extends SocialContextualParser>> exclusions = new HashSet<>();

    @Builder.Default
    private boolean restrictToPlayerInputParsers = false;

    @Getter(AccessLevel.PRIVATE)
    private final Deque<SocialContextualParser> parserQueue = new ArrayDeque<>();

    /**
     * Creates a processor that uses the same parser list as
     * {@link ovh.mythmc.social.api.text.GlobalTextProcessor#getContextualParsers()}.
     *
     * @return a default processor
     */
    public static CustomTextProcessor defaultProcessor() {
        return CustomTextProcessor.builder()
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
        SocialProcessorContext processorContext = SocialProcessorContext.from(context, this);

        // Populate queue, respecting any exclusions
        parserQueue.clear();
        parserQueue.addAll(getWithExclusions());

        // HashMap tracking avoids O(n) Collections.frequency() + List.copyOf() per
        // iteration
        final Map<Class<? extends SocialContextualParser>, Integer> callCounts = new HashMap<>();

        while (!parserQueue.isEmpty()) {
            final SocialContextualParser parser = parserQueue.removeFirst();

            if (parser instanceof SocialFilterLike && !restrictToPlayerInputParsers)
                continue;
            if (!(parser instanceof SocialUserInputParser) && restrictToPlayerInputParsers)
                continue;
            if (!parser.supportsOfflinePlayers() && !context.user().isOnline())
                continue;

            final Class<? extends SocialContextualParser> parserClass = parser.getClass();
            final int callCount = callCounts.getOrDefault(parserClass, 0);
            if (callCount >= 3) {
                Social.get().getLogger().warn(
                        "Parser {} has been called too many times. This can potentially degrade performance. " +
                                "Please, inform the author(s) of {} about this.",
                        parserClass.getName(), parserClass.getName());
                break;
            }
            callCounts.put(parserClass, callCount + 1);
            processorContext.addAppliedParser(parserClass);

            try {
                final Component parsed = parser.parse(processorContext);
                final Component withHover = parseHoverText(parser, processorContext.withMessage(parsed));
                processorContext = SocialProcessorContext.from(processorContext.withMessage(withHover), this);
            } catch (Exception e) {
                Social.get().getLogger().error("Parser {} couldn't be applied: {}", parserClass.getSimpleName(), e);
                e.printStackTrace(System.err);
            }
        }

        // Process injections
        for (SocialInjectedValue<?> injectedValue : processorContext.injectedValues()) {
            final Component parsed = injectedValue.parse(processorContext);
            processorContext = SocialProcessorContext.from(processorContext.withMessage(parsed), this);
        }

        return processorContext.message();
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

    /**
     * Adds a parser to the processing queue while parsing is in progress.
     *
     * <p>
     * The injected parser will be executed after all currently queued parsers.
     *
     * @param parser the parser to inject
     */
    public void injectParser(@NotNull SocialContextualParser parser) {
        parserQueue.add(parser);
    }

    // -- private helpers --

    private List<SocialContextualParser> getWithExclusions() {
        if (exclusions.isEmpty())
            return List.copyOf(parsers);
        return parsers.stream()
                .filter(parser -> !exclusions.contains(parser.getClass()))
                .toList();
    }

    private static Component parseHoverText(@NotNull SocialContextualParser parser,
            @NotNull SocialParserContext context) {
        final var hoverEvent = context.message().hoverEvent();
        if (hoverEvent != null && hoverEvent.action() == Action.SHOW_TEXT) {
            @SuppressWarnings("unchecked")
            final Component hoverText = ((HoverEvent<Component>) hoverEvent).value();
            return context.message().hoverEvent(parser.parse(context.withMessage(hoverText)));
        }
        return context.message();
    }

}
