package ovh.mythmc.social.api.text.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Experimental;

import lombok.Builder;
import lombok.Singular;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialProcessorContext;
import ovh.mythmc.social.api.text.CustomTextProcessor;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.text.parser.SocialUserInputParser;

@Experimental
@Builder
public class SocialParserGroup implements SocialUserInputParser {

    @Singular("parser") private final List<SocialContextualParser> content = new ArrayList<>();

    public List<SocialContextualParser> get() {
        return List.copyOf(content);
    }

    public void add(final @NotNull SocialContextualParser... parsers) {
        content.addAll(Arrays.asList(parsers));
    }

    public void remove(final @NotNull SocialContextualParser... parsers) {
        Arrays.stream(parsers).forEach(content::remove);
    }

    public void removeAll() {
        get().forEach(content::remove);
    }

    @Override
    public boolean supportsOfflinePlayers() {
        return true;
    }

    @Experimental
    public Component requestToGroup(@NotNull SocialContextualParser requester, @NotNull SocialParserContext context) {
        final CustomTextProcessor processor = CustomTextProcessor.builder()
            .parsers(content.stream()
                .filter(parser -> !parser.getClass().equals(requester.getClass()))
                .toList())
            .build();

        return processor.parse(context.withGroup(Optional.of(this))); 
    }

    @SuppressWarnings("unchecked")
    public <T extends SocialContextualParser> List<T> getByType(@NotNull Class<T> type) {
        final List<T> typeParsers = new ArrayList<>();

        content.stream()
            .filter(type::isInstance)
            .map(parser -> (T) parser)
            .forEach(typeParsers::add);

        content.stream() // Recursive search
            .filter(parser -> parser instanceof SocialParserGroup)
            .map(parser -> (SocialParserGroup) parser)
            .forEach(group -> typeParsers.addAll(group.getByType(type)));

        return typeParsers;
    }

    @Override
    public Component parse(SocialParserContext context) {
        if (context instanceof SocialProcessorContext processorContext) {
            final CustomTextProcessor processor = CustomTextProcessor.builder()
                .parsers(content)
                .playerInput(processorContext.processor().playerInput())
                .build();

            return processor.parse(processorContext.withGroup(Optional.of(this))); 
        }

        return context.message();
    }

}
