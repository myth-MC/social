package ovh.mythmc.social.api.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
        Component message = context.message();

        message = parseByPriority(context.withMessage(message), SocialParserProperties.ParserPriority.HIGH);
        message = parseByPriority(context.withMessage(message), SocialParserProperties.ParserPriority.NORMAL);
        message = parseByPriority(context.withMessage(message), SocialParserProperties.ParserPriority.LOW);

        return message;
    }

    private Component parseByPriority(SocialParserContext context, SocialParserProperties.ParserPriority priority) {
        Component message = context.message();

        for (SocialParser parser : getByPriority(priority)) {
            if (exclusions.contains(parser.getClass()))
                continue;

            if (parser instanceof SocialFilterLike && playerInput == false)
                continue;

            if (!(parser instanceof SocialPlayerInputParser) && playerInput)
                continue;

            if (parser instanceof SocialContextualParser contextualParser) {
                message = contextualParser.parse(context.withMessage(message));
            } else {
                message = parser.parse(context.socialPlayer(), message);
            }
        }

        return message;
    }

    private List<SocialParser> getByPriority(final @NotNull SocialParserProperties.ParserPriority priority) {
        List<SocialParser> socialParserList = new ArrayList<>();
        for (SocialParser parser : parsers) {
            SocialParserProperties.ParserPriority parserPriority = SocialParserProperties.ParserPriority.NORMAL;
            if (parser.getClass().isAnnotationPresent(SocialParserProperties.class))
                parserPriority = parser.getClass().getAnnotation(SocialParserProperties.class).priority();

            if (parserPriority.equals(priority))
                socialParserList.add(parser);
        }

        return socialParserList;
    }
    
}
