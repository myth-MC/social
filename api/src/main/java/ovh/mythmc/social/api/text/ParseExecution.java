package ovh.mythmc.social.api.text;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.Action;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialProcessorContext;
import ovh.mythmc.social.api.text.filter.SocialFilterLike;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.text.parser.SocialUserInputParser;

public class ParseExecution {

    private final TextProcessor processor;
    private SocialProcessorContext context;

    private final Deque<SocialContextualParser> queue;
    private final Map<Class<?>, Integer> callCounts = new HashMap<>();

    private final long timeoutMillis;
    private boolean timeoutExceeded = false;

    ParseExecution(@NotNull TextProcessor processor, @NotNull SocialParserContext baseContext,
            long timeoutMillis) {
        this.processor = processor;
        this.context = SocialProcessorContext.from(baseContext, processor, this);
        this.timeoutMillis = timeoutMillis;

        final List<SocialContextualParser> parserList = processor.getWithExclusions();
        this.queue = new ArrayDeque<>(parserList.size());
        this.queue.addAll(parserList);
    }

    public void inject(@NotNull SocialContextualParser parser) {
        queue.add(parser);
    }

    @NotNull
    Component run() {
        // Start the timeout task
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> timeoutExceeded = true, timeoutMillis, TimeUnit.MILLISECONDS);

        try {
            while (!queue.isEmpty() && !timeoutExceeded) {
                final SocialContextualParser parser = queue.removeFirst();
                if (shouldSkip(parser))
                    continue;

                if (exceededCallLimit(parser)) {
                    logExcessiveParserCalls(parser);
                    break;
                }

                try {
                    processParser(parser);
                } catch (Exception e) {
                    logError(parser, e);
                }
            }

            // Process injections
            processInjections();
        } finally {
            // Shut down the executor after timeout
            executor.shutdownNow();
        }

        // If the timeout is exceeded, log a warning to gthe console
        if (timeoutExceeded) {
            Social.get().getLogger().warn("Timeout of {}ms exceeded during parsing", timeoutMillis);
        }

        return context.message();
    }

    private boolean shouldSkip(@NotNull SocialContextualParser parser) {
        if (parser instanceof SocialFilterLike && !processor.restrictToPlayerInputParsers())
            return true;
        if (!(parser instanceof SocialUserInputParser) && processor.restrictToPlayerInputParsers())
            return true;
        if (!parser.supportsOfflinePlayers() && !context.user().isOnline())
            return true;

        return false;
    }

    private boolean exceededCallLimit(@NotNull SocialContextualParser parser) {
        final Class<? extends SocialContextualParser> parserClass = parser.getClass();
        final int callCount = callCounts.getOrDefault(parserClass, 0);
        return callCount >= 3;
    }

    private void incrementCallCount(@NotNull SocialContextualParser parser) {
        callCounts.compute(parser.getClass(), (key, count) -> count == null ? 1 : count + 1);
    }

    private void processParser(@NotNull SocialContextualParser parser) {
        incrementCallCount(parser);
        context.addAppliedParser(parser.getClass());

        final Component parsed = parser.parse(context);
        final Component withHover = parseHoverText(parser, context.withMessage(parsed));
        context = SocialProcessorContext.from(context.withMessage(withHover), processor, this);
    }

    private static void logError(@NotNull SocialContextualParser parser, @NotNull Exception exception) {
        Social.get().getLogger().error("Error applying parser {}: {}", parser.getClass().getSimpleName(),
                exception.getMessage());
        exception.printStackTrace(System.err);
    }

    private static void logExcessiveParserCalls(SocialContextualParser parser) {
        Social.get().getLogger().warn(
                "Parser {} has been called too many times. This can potentially degrade performance. " +
                        "Please, inform the author(s) of {} about this.",
                parser.getClass().getName(), parser.getClass().getName());
    }

    private void processInjections() {
        for (SocialInjectedValue<?, ?> injectedValue : context.injectedValues()) {
            final Component parsed = injectedValue.parse(context);
            context = SocialProcessorContext.from(context.withMessage(parsed), processor, this);
        }
    }

    private static Component parseHoverText(@NotNull SocialContextualParser parser,
            @NotNull SocialParserContext context) {
        // Retrieve the hover event from the message
        HoverEvent<?> hoverEvent = context.message().hoverEvent(); // use raw type for now

        // Check if the hover event is non-null and of type SHOW_TEXT
        if (hoverEvent != null && hoverEvent.action() == Action.SHOW_TEXT) {
            // Safely cast it to HoverEvent<Component> and extract the hover text
            if (hoverEvent.value() instanceof Component hoverText) {
                return context.message().hoverEvent(parser.parse(context.withMessage(hoverText)));
            }
        }
        return context.message();
    }

}
