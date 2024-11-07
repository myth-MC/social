package ovh.mythmc.social.api.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.annotations.SocialParserProperties;
import ovh.mythmc.social.api.text.filters.SocialFilterLike;
import ovh.mythmc.social.api.text.parsers.SocialContextualParser;
import ovh.mythmc.social.api.text.parsers.SocialParser;
import ovh.mythmc.social.api.text.parsers.SocialPlayerInputParser;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@Data
@With
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@SuppressWarnings("deprecation")
public class CustomTextProcessor {

    @Builder.Default
    private Collection<SocialParser> parsers = new ArrayList<>();

    @Builder.Default
    private Collection<Class<?>> exclusions = new ArrayList<>();

    @Builder.Default
    private boolean playerInput = false;

    public static CustomTextProcessor defaultProcessor() {
        List<SocialParser> parserList = new ArrayList<>();
        Social.get().getTextProcessor().getParsers().forEach(parser -> {
            parserList.add(parser);
        });

        return CustomTextProcessor.builder()
            .parsers(parserList)
            .build();
    }

    public Component parse(SocialParserContext context) {
        context = context.withTextProcessor(this);

        for (SocialParserProperties.ParserPriority priority : SocialParserProperties.ParserPriority.values()) {
            context = parseByPriority(context, priority);
        }

        return context.message();
    }

    private SocialParserContext parseByPriority(SocialParserContext context, SocialParserProperties.ParserPriority priority) {
        for (SocialParser parser : getByPriority(priority)) {
            if (parser instanceof SocialFilterLike && playerInput == false)
                continue;

            if (!(parser instanceof SocialPlayerInputParser) && playerInput)
                continue;

            List<Class<?>> appliedParsers = context.appliedParsers();
            if (Collections.frequency(appliedParsers, parser.getClass()) > 2) {
                Social.get().getLogger().warn("Parser " + parser.getClass().getName() + " has been called twice. This can potentially degrade performance. Please, inform the author of " + parser.getClass().getName() + " about this. THIS IS NOT RELATED WITH SOCIAL.");
                break;
            }

            appliedParsers.add(parser.getClass());
            context = context.withAppliedParsers(appliedParsers);

            if (parser instanceof SocialContextualParser contextualParser) {
                context = context.withMessage(contextualParser.parse(context));
            } else {
                context = context.withMessage(parser.parse(context.socialPlayer(), context.message()));
            }
        }

        return context;
    }

    private List<SocialParser> getByPriority(final @NotNull SocialParserProperties.ParserPriority priority) {
        List<SocialParser> socialParserList = new ArrayList<>();
        for (SocialParser parser : getWithExclusions()) {
            SocialParserProperties.ParserPriority parserPriority = SocialParserProperties.ParserPriority.NORMAL;
            if (parser.getClass().isAnnotationPresent(SocialParserProperties.class))
                parserPriority = parser.getClass().getAnnotation(SocialParserProperties.class).priority();

            if (parserPriority.equals(priority))
                socialParserList.add(parser);
        }

        return socialParserList;
    }

    private List<SocialParser> getWithExclusions() {
        return parsers.stream()
            .filter(parser -> !exclusions.contains(parser.getClass()))
            .toList();
    }
    
}
