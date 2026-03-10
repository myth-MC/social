package ovh.mythmc.social.api.text;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.Action;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialProcessorContext;
import ovh.mythmc.social.api.logger.LoggerWrapper;
import ovh.mythmc.social.api.text.filter.SocialFilterLike;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.text.parser.SocialUserInputParser;

public class ParseExecution {

    private static final int MAX_PARSER_CALLS = 3;

    private final TextProcessor processor;
    private SocialProcessorContext context;

    private final ArrayDeque<SocialContextualParser> queue;
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
        final var logger = Social.get().getLogger();
        final boolean debug = Social.get().getConfig().getGeneral().isDebug();

        final DebugMetrics metrics = debug ? new DebugMetrics() : null;
        final long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMillis);

        while (!queue.isEmpty()) {
            if (System.nanoTime() >= deadline) {
                timeoutExceeded = true;
                break;
            }

            final SocialContextualParser parser = queue.removeFirst();

            if (shouldSkip(parser))
                continue;

            if (exceededCallLimit(parser)) {
                logExcessiveParserCalls(parser);
                break;
            }

            final long start = debug ? System.nanoTime() : 0;

            try {
                processParser(parser);
            } catch (Exception e) {
                logError(parser, e);
            }

            if (metrics != null)
                metrics.record(parser.getClass(), System.nanoTime() - start);
        }

        processInjections();

        if (timeoutExceeded)
            logger.warn("Timeout of {}ms exceeded during parsing", timeoutMillis);

        if (metrics != null)
            metrics.log(logger);

        return context.message();
    }

    private boolean shouldSkip(@NotNull SocialContextualParser parser) {
        return (parser instanceof SocialFilterLike && !processor.restrictToPlayerInputParsers())
                || (!(parser instanceof SocialUserInputParser) && processor.restrictToPlayerInputParsers())
                || (!parser.supportsOfflinePlayers() && !context.user().isOnline());
    }

    private boolean exceededCallLimit(@NotNull SocialContextualParser parser) {
        return callCounts.getOrDefault(parser.getClass(), 0) >= MAX_PARSER_CALLS;
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

    private void processInjections() {
        for (SocialInjectedValue<?, ?> injectedValue : context.injectedValues()) {
            final Component parsed = injectedValue.parse(context);
            context = SocialProcessorContext.from(context.withMessage(parsed), processor, this);
        }
    }

    private static Component parseHoverText(@NotNull SocialContextualParser parser,
            @NotNull SocialParserContext context) {
        final HoverEvent<?> hoverEvent = context.message().hoverEvent();
        if (hoverEvent != null && hoverEvent.action() == Action.SHOW_TEXT
                && hoverEvent.value() instanceof Component hoverText) {
            return context.message().hoverEvent(parser.parse(context.withMessage(hoverText)));
        }
        return context.message();
    }

    private static void logError(@NotNull SocialContextualParser parser, @NotNull Exception exception) {
        Social.get().getLogger().error("Error applying parser {}: {}",
                parser.getClass().getSimpleName(), exception.getMessage());
        exception.printStackTrace(System.err);
    }

    private static void logExcessiveParserCalls(@NotNull SocialContextualParser parser) {
        final String name = parser.getClass().getName();
        Social.get().getLogger().warn(
                "Parser {} has been called too many times. This can potentially degrade performance. " +
                        "Please, inform the author(s) of {} about this.",
                name, name);
    }

    private static final class DebugMetrics {

        private final long startNs = System.nanoTime();

        private long totalParserNs = 0;
        private int parserCalls = 0;

        private long slow1 = 0, slow2 = 0, slow3 = 0;
        private Class<?> slowType1 = null, slowType2 = null, slowType3 = null;

        void record(@NotNull Class<?> type, long elapsedNs) {
            totalParserNs += elapsedNs;
            parserCalls++;

            if (elapsedNs > slow1) {
                slow3 = slow2; slowType3 = slowType2;
                slow2 = slow1; slowType2 = slowType1;
                slow1 = elapsedNs; slowType1 = type;
            } else if (elapsedNs > slow2) {
                slow3 = slow2; slowType3 = slowType2;
                slow2 = elapsedNs; slowType2 = type;
            } else if (elapsedNs > slow3) {
                slow3 = elapsedNs; slowType3 = type;
            }
        }

        void log(@NotNull LoggerWrapper logger) {
            final long pipelineMs = (System.nanoTime() - startNs) / 1_000_000;
            final long parserTotalMs = totalParserNs / 1_000_000;
            final long avgParserUs = parserCalls == 0 ? 0 : (totalParserNs / parserCalls) / 1_000;

            logger.info(
                "Parser pipeline: {} ms | parser calls: {} | parser total: {} ms | avg parser: {} µs",
                pipelineMs, parserCalls, parserTotalMs, avgParserUs
            );

            if (slowType1 != null) logger.info("Slow parser #1: {} ({} µs)", slowType1.getSimpleName(), slow1 / 1_000);
            if (slowType2 != null) logger.info("Slow parser #2: {} ({} µs)", slowType2.getSimpleName(), slow2 / 1_000);
            if (slowType3 != null) logger.info("Slow parser #3: {} ({} µs)", slowType3.getSimpleName(), slow3 / 1_000);
        }
    }

}
