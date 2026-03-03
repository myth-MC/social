package ovh.mythmc.social.api.context;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.text.CustomTextProcessor;
import ovh.mythmc.social.api.text.ParseExecution;
import ovh.mythmc.social.api.text.exception.ParseExecutionNotAvailable;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Represents a processing-phase context for a social message.
 *
 * <p>
 * {@code SocialProcessorContext} extends {@link SocialParserContext} and is used
 * during active message processing. It provides additional state and utilities
 * required while parsers are being executed.
 * </p>
 *
 * <p>
 * In addition to all parsing context data, this context includes:
 * </p>
 * <ul>
 *     <li>The active {@link CustomTextProcessor}</li>
 *     <li>The {@link ParseExecution} controlling the current parse lifecycle</li>
 *     <li>A list of applied {@link SocialContextualParser parsers}</li>
 * </ul>
 *
 * @see SocialParserContext
 * @see CustomTextProcessor
 * @see ParseExecution
 * @see SocialContextualParser
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
@Experimental
public class SocialProcessorContext extends SocialParserContext {

    /**
     * The processor responsible for handling the current message transformation.
     */
    @Getter
    private final CustomTextProcessor processor;

    private final List<Class<? extends SocialContextualParser>> appliedParsers;

    private final ParseExecution execution;

    SocialProcessorContext(
            SocialUser user,
            ChatChannel channel,
            Component message,
            ChatChannel.ChannelType messageChannelType,
            SocialParserGroup group,
            List<SocialInjectedValue<?, ?>> injectedValues,
            CustomTextProcessor processor,
            ParseExecution execution) {

        super(user, channel, message, messageChannelType, group, injectedValues);
        this.processor = processor;
        this.appliedParsers = new ArrayList<>();
        this.execution = execution;
    }

    /**
     * Adds a parser class to the list of applied parsers.
     *
     * <p>
     * This method is intended for internal use only.
     * </p>
     *
     * @param appliedParser the parser class that was executed
     */
    @Internal
    public void addAppliedParser(Class<? extends SocialContextualParser> appliedParser) {
        this.appliedParsers.add(appliedParser);
    }

    /**
     * Returns an immutable copy of all parser classes that have been
     * applied during this processing cycle.
     *
     * @return an unmodifiable list of applied parser classes
     */
    public List<Class<? extends SocialContextualParser>> appliedParsers() {
        return List.copyOf(appliedParsers);
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
        if (execution == null)
            throw new ParseExecutionNotAvailable();

        execution.inject(parser);
    }

    /**
     * Creates a {@link SocialProcessorContext} from an existing
     * {@link SocialParserContext}.
     *
     * <p>
     * All base parsing data is copied from the provided context, and the
     * supplied processor and execution are attached.
     * </p>
     *
     * @param context   the base parser context
     * @param processor the active text processor
     * @param execution the current parse execution
     * @return a new processor context instance
     */
    public static SocialProcessorContext from(SocialParserContext context, CustomTextProcessor processor,
            ParseExecution execution) {
        return new SocialProcessorContext(
                context.user(),
                context.channel(),
                context.message(),
                context.messageChannelType(),
                context.group().orElse(null),
                context.injectedValues(),
                processor,
                execution);
    }

}
