package ovh.mythmc.social.api.text;

import java.util.*;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.Action;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialProcessorContext;
import ovh.mythmc.social.api.text.filter.SocialFilterLike;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.text.parser.SocialIdentifiedParser;
import ovh.mythmc.social.api.text.parser.SocialUserInputParser;

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
    private final Collection<Class<? extends SocialContextualParser>> exclusions = new ArrayList<>();

    @Builder.Default
    private boolean playerInput = false;

    @Getter(AccessLevel.PRIVATE)
    private final List<SocialContextualParser> parserQueue = new ArrayList<>();

    public static CustomTextProcessor defaultProcessor() {
        return CustomTextProcessor.builder()
            .parsers(Social.get().getTextProcessor().getContextualParsers())
            .build();
    }

    public Component parse(@NotNull SocialParserContext context) {
        SocialProcessorContext processorContext = SocialProcessorContext.from(context, this);

        initializeQueue(getWithExclusions());

        while (!parserQueue.isEmpty()) {
            final SocialContextualParser parser = parserQueue.getFirst();

            // Remove parser from queue
            parserQueue.remove(parser);

            if (parser instanceof SocialFilterLike && !playerInput)
                continue;

            if (!(parser instanceof SocialUserInputParser) && playerInput)
                continue;

            if (!parser.supportsOfflinePlayers() && !context.user().isOnline())
                continue;

            if (Collections.frequency(processorContext.appliedParsers(), parser.getClass()) > 3) {
                Social.get().getLogger().warn("Parser " + parser.getClass().getName() + " has been called too many times. This can potentially degrade performance. Please, inform the author(s) of " + parser.getClass().getName() + " about this.");
                break;
            }

            processorContext.addAppliedParser(parser.getClass());

            try {
                processorContext = SocialProcessorContext.from(processorContext.withMessage(parser.parse(processorContext)), this);
                processorContext = SocialProcessorContext.from(processorContext.withMessage(parseHoverText(parser, processorContext)), this);
            } catch (Throwable t) {
                Social.get().getLogger().error(
                    "Parser {} couldn't be applied: {}",
                    parser.getClass().getSimpleName(),
                    t
                );

                t.printStackTrace(System.err);
            }


            // Workaround to parse the HoverEvent
            /*
            if (processorContext.message().hoverEvent() != null && processorContext.message().hoverEvent().action().equals(Action.SHOW_TEXT)) {
                @SuppressWarnings("unchecked")
                Component hoverText = ((HoverEvent<Component>) processorContext.message().hoverEvent()).value();

                Component messageWithHoverText = processorContext.message()
                        .hoverEvent(parser.parse(processorContext.withMessage(hoverText)));

                processorContext = SocialProcessorContext.from(processorContext.withMessage(messageWithHoverText), this);
            }

             */
        }

        // Process injections
        for (SocialInjectedValue<?> injectedValue : processorContext.injectedValues()) {
            Component message = processorContext.message();

            message = injectedValue.parse(processorContext.withMessage(message));
            processorContext = SocialProcessorContext.from(processorContext.withMessage(message), this);
        }

        return processorContext.message();
    }

    @SuppressWarnings("unchecked")
    public <T extends SocialContextualParser> List<T> getContextualParsersByType(final @NotNull Class<T> type) {
        final List<T> typeParsers = new ArrayList<>();

        parsers.stream()
            .filter(type::isInstance)
            .map(parser -> (T) parser)
            .forEach(typeParsers::add);

        parsers.stream()
            .filter(parser -> parser instanceof SocialParserGroup)
            .map(parser -> (SocialParserGroup) parser)
            .forEach(group -> typeParsers.addAll(group.getByType(type)));

        return typeParsers;
    }

    public Optional<SocialParserGroup> getGroupByContextualParser(final @NotNull Class<? extends SocialContextualParser> parserClass) {
        return getContextualParsersByType(SocialParserGroup.class).stream()
            .filter(group -> !group.getByType(parserClass).isEmpty())
            .findFirst();
    }

    public <T extends SocialIdentifiedParser> Optional<T> getIdentifiedContextualParser(final @NotNull Class<T> type, final @NotNull String identifier) {
        return getContextualParsersByType(type).stream()
            .filter(parser -> parser.identifier().equals(identifier))
            .findFirst();
    }

    private void initializeQueue(@NotNull List<SocialContextualParser> parserQueue) {
        this.parserQueue.clear();
        this.parserQueue.addAll(parserQueue);
    }

    private List<SocialContextualParser> getWithExclusions() {
        return parsers.stream()
            .filter(parser -> !exclusions.contains(parser.getClass()))
            .toList();
    }

    public void injectParser(@NotNull SocialContextualParser parser) {
        this.parserQueue.add(parser);
    }

    private static Component parseHoverText(@NotNull SocialContextualParser parser, @NotNull SocialProcessorContext context) {
        Component message = context.message();

        // Workaround to parse the HoverEvent
        if (context.message().hoverEvent() != null && context.message().hoverEvent().action().equals(Action.SHOW_TEXT)) {
            @SuppressWarnings("unchecked")
            Component hoverText = ((HoverEvent<Component>) context.message().hoverEvent()).value();

            message = context.message()
                .hoverEvent(parser.parse(context.withMessage(hoverText)));
        }

        return message;
    }
    
}
