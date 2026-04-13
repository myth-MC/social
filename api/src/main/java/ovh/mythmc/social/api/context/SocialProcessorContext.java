package ovh.mythmc.social.api.context;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.text.TextProcessor;
import ovh.mythmc.social.api.text.ParseExecution;
import ovh.mythmc.social.api.text.exception.ParseExecutionNotAvailable;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Represents a processing-phase context for a social message.
 */
@Experimental
public class SocialProcessorContext extends SocialParserContext {

    private final TextProcessor processor;
    private final List<Class<? extends SocialContextualParser>> appliedParsers;
    private final ParseExecution execution;

    SocialProcessorContext(
            @NotNull SocialUser user,
            @NotNull ChatChannel channel,
            @NotNull Component message,
            @NotNull ChatChannel.ChannelType messageChannelType,
            @Nullable SocialParserGroup group,
            @NotNull List<SocialInjectedValue<?, ?>> injectedValues,
            @NotNull TextProcessor processor,
            @Nullable ParseExecution execution) {

        super(user, channel, message, messageChannelType, group, injectedValues);
        this.processor = processor;
        this.appliedParsers = new ArrayList<>();
        this.execution = execution;
    }

    /**
     * Returns the processor responsible for handling the current message transformation.
     */
    public @NotNull TextProcessor processor() {
        return processor;
    }

    /**
     * Adds a parser class to the list of applied parsers.
     */
    @Internal
    public void addAppliedParser(@NotNull Class<? extends SocialContextualParser> appliedParser) {
        this.appliedParsers.add(appliedParser);
    }

    /**
     * Returns an immutable copy of all parser classes that have been
     * applied during this processing cycle.
     */
    public @NotNull List<Class<? extends SocialContextualParser>> appliedParsers() {
        return List.copyOf(appliedParsers);
    }

    /**
     * Adds a parser to the processing queue while parsing is in progress.
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
     * @param context   the base parser context
     * @param processor the active text processor
     * @param execution the current parse execution
     * @return a new processor context instance
     */
    public static @NotNull SocialProcessorContext from(@NotNull SocialParserContext context, @NotNull TextProcessor processor,
            @Nullable ParseExecution execution) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SocialProcessorContext that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(processor, that.processor) &&
                Objects.equals(appliedParsers, that.appliedParsers) &&
                Objects.equals(execution, that.execution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), processor, appliedParsers, execution);
    }

    @Override
    public String toString() {
        return "SocialProcessorContext{" +
                "user=" + user() +
                ", channel=" + channel() +
                ", processor=" + processor +
                ", appliedParsers=" + appliedParsers +
                '}';
    }
}

