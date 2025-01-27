package ovh.mythmc.social.api.text;

import java.util.ArrayList;
import java.util.List;
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
import ovh.mythmc.social.api.text.filters.SocialFilterLike;
import ovh.mythmc.social.api.text.parsers.SocialContextualParser;
import ovh.mythmc.social.api.text.parsers.SocialUserInputParser;

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
    private Collection<Class<?>> exclusions = new ArrayList<>();

    @Builder.Default
    private boolean playerInput = false;

    public static CustomTextProcessor defaultProcessor() {
        return CustomTextProcessor.builder()
            .parsers(Social.get().getTextProcessor().getContextualParsers())
            .build();
    }

    public Component parse(SocialParserContext context) {
        for (SocialContextualParser parser : getWithExclusions()) {
            if (parser instanceof SocialFilterLike && playerInput == false)
                continue;

            if (!(parser instanceof SocialUserInputParser) && playerInput)
                continue;

            List<Class<?>> appliedParsers = context.appliedParsers();
            if (Collections.frequency(appliedParsers, parser.getClass()) > 3) {
                Social.get().getLogger().warn("Parser " + parser.getClass().getName() + " has been called too many times. This can potentially degrade performance. Please, inform the author(s) of " + parser.getClass().getName() + " about this.");
                break;
            }

            appliedParsers.add(parser.getClass());
            context = context
                .withAppliedParsers(appliedParsers)
                .withMessage(parser.parse(context));

            // Workaround to parse the HoverEvent
            if (context.message().hoverEvent() != null && context.message().hoverEvent().action().equals(Action.SHOW_TEXT)) {
                @SuppressWarnings("unchecked")
                Component hoverText = ((HoverEvent<Component>) context.message().hoverEvent()).value();

                Component messageWithHoverText = context.message()
                    .hoverEvent(parser.parse(context.withMessage(hoverText)));

                context = context
                    .withMessage(messageWithHoverText);
            }
        }

        return context.message();
    }

    private List<SocialContextualParser> getWithExclusions() {
        return parsers.stream()
            .filter(parser -> !exclusions.contains(parser.getClass()))
            .toList();
    }
    
}
