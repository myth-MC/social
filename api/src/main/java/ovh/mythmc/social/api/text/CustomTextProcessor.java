package ovh.mythmc.social.api.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.Action;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialProcessorContext;
import ovh.mythmc.social.api.text.filter.SocialFilterLike;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
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
    private Collection<SocialContextualParser> parsers = new ArrayList<>();

    @Builder.Default
    private Collection<Class<? extends SocialContextualParser>> exclusions = new ArrayList<>();

    @Builder.Default
    private boolean playerInput = false;

    public static CustomTextProcessor defaultProcessor() {
        return CustomTextProcessor.builder()
            .parsers(Social.get().getTextProcessor().getContextualParsers())
            .build();
    }

    public Component parse(SocialParserContext context) {
        SocialProcessorContext processorContext = SocialProcessorContext.from(context, this);
        
        for (SocialContextualParser parser : getWithExclusions()) {
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
            processorContext = SocialProcessorContext.from(processorContext.withMessage(parser.parse(processorContext)), this);

            // Workaround to parse the HoverEvent
            if (processorContext.message().hoverEvent() != null && processorContext.message().hoverEvent().action().equals(Action.SHOW_TEXT)) {
                @SuppressWarnings("unchecked")
                Component hoverText = ((HoverEvent<Component>) processorContext.message().hoverEvent()).value();

                Component messageWithHoverText = processorContext.message()
                    .hoverEvent(parser.parse(processorContext.withMessage(hoverText)));

                processorContext = SocialProcessorContext.from(processorContext.withMessage(messageWithHoverText), this);
            }
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

    private List<SocialContextualParser> getWithExclusions() {
        return parsers.stream()
            .filter(parser -> !exclusions.contains(parser.getClass()))
            .toList();
    }
    
}
