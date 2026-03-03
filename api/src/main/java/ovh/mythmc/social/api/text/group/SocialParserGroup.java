package ovh.mythmc.social.api.text.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

/**
 * Represents a group of parsers which should be processed in a single batch.
 */
@Experimental
@Builder
public class SocialParserGroup implements SocialUserInputParser {

    @Singular("parser")
    private final List<SocialContextualParser> content = new ArrayList<>();

    /**
     * Gets the unmodifiable {@link List} of {@link SocialContextualParser}s.
     * @return a {@link List} with every {@link SocialContextualParser} in this group
     */
    public List<SocialContextualParser> get() {
        return Collections.unmodifiableList(content);
    }

    /**
     * Adds one or various {@link SocialContextualParser}s to this group.
     * @param parsers the parser or parsers to add
     */
    public void add(final @NotNull SocialContextualParser... parsers) {
        content.addAll(Arrays.asList(parsers));
    }

    /**
     * Removes one or various {@link SocialContextualParser}s from this group.
     * @param parsers the parser or parsers to remove
     */
    public void remove(final @NotNull SocialContextualParser... parsers) {
        Arrays.stream(parsers).forEach(content::remove);
    }

    /**
     * Removes all {@link SocialContextualParser}s in this group.
     */
    public void removeAll() {
        get().forEach(content::remove);
    }

    @Override
    public boolean supportsOfflinePlayers() {
        return true;
    }

    /**
     * Requests a specific {@link SocialParserContext} to this group and processes it.
     * 
     * <p>
     * This method is designed to be called from a {@link SocialContextualParser} inside
     * the group without causing any recursion issues in runtime.
     * </p>
     * @param requester the {@link SocialContextualParser} doing the request
     * @param context   the {@link SocialParserContext} to parse
     * @return          the resulting {@link Component} from the request
     */
    @Experimental
    public Component requestToGroup(@NotNull SocialContextualParser requester, @NotNull SocialParserContext context) {
        final CustomTextProcessor processor = CustomTextProcessor.builder()
                .parsers(content.stream()
                        .filter(parser -> !parser.getClass().equals(requester.getClass()))
                        .toList())
                .build();

        return processor.parse(context.withGroup(this));
    }

    /**
     * Gets every parser matching a specific type in the group.
     * 
     * <p>
     * For example, {@code getByType(SocialContextualPlaceholder.class)} would return
     * every {@link ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder} in
     * this group.
     * </p>
     * @param <T>  the type of {@link SocialContextualParser} to get
     * @param type the type of {@link SocialContextualParser} to get
     * @return     an unmodifiable {@link List} with every parser of this type
     */
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
                    .restrictToPlayerInputParsers(processorContext.processor().restrictToPlayerInputParsers())
                    .build();

            return processor.parse(processorContext.withGroup(this));
        }

        return context.message();
    }

}
