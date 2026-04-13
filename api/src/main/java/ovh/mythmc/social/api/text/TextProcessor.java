package ovh.mythmc.social.api.text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.text.parser.SocialIdentifiedParser;

/**
 * A configurable text processor that applies a specific parser list to a
 * {@link ovh.mythmc.social.api.context.SocialParserContext}.
 */
public final class TextProcessor {

    private final List<SocialContextualParser> parsers;
    private final Set<Class<? extends SocialContextualParser>> exclusions;
    private final boolean restrictToPlayerInputParsers;

    TextProcessor(@NotNull List<SocialContextualParser> parsers,
                  @NotNull Set<Class<? extends SocialContextualParser>> exclusions,
                  boolean restrictToPlayerInputParsers) {
        this.parsers = List.copyOf(parsers);
        this.exclusions = Set.copyOf(exclusions);
        this.restrictToPlayerInputParsers = restrictToPlayerInputParsers;
    }

    /**
     * Creates a new builder for {@link TextProcessor}.
     */
    public static @NotNull TextProcessorBuilder builder() {
        return new TextProcessorBuilder();
    }

    /**
     * Creates a processor that mirrors the default global parser configuration.
     */
    public static @NotNull TextProcessor defaultProcessor() {
        return TextProcessor.builder()
                .parsers(Social.get().getTextProcessor().getContextualParsers())
                .build();
    }

    /**
     * Returns the list of parsers.
     */
    public @NotNull List<SocialContextualParser> parsers() {
        return parsers;
    }

    /**
     * Returns the set of excluded parser classes.
     */
    public @NotNull Set<Class<? extends SocialContextualParser>> exclusions() {
        return exclusions;
    }

    /**
     * Returns whether to restrict processing to player input parsers.
     */
    public boolean restrictToPlayerInputParsers() {
        return restrictToPlayerInputParsers;
    }

    /**
     * Returns a new processor with the specified parsers.
     */
    public @NotNull TextProcessor withParsers(@NotNull List<SocialContextualParser> parsers) {
        return new TextProcessor(parsers, exclusions, restrictToPlayerInputParsers);
    }

    /**
     * Returns a new processor with the specified exclusions.
     */
    public @NotNull TextProcessor withExclusions(@NotNull Set<Class<? extends SocialContextualParser>> exclusions) {
        return new TextProcessor(parsers, exclusions, restrictToPlayerInputParsers);
    }

    /**
     * Returns a new processor with player input restriction toggled.
     */
    public @NotNull TextProcessor withRestrictToPlayerInputParsers(boolean restrictToPlayerInputParsers) {
        return new TextProcessor(parsers, exclusions, restrictToPlayerInputParsers);
    }

    /**
     * Runs all parsers in order against the given context and returns the final component.
     */
    public @NotNull Component parse(@NotNull SocialParserContext context) {
        ParseExecution execution = new ParseExecution(this, context, 5000);
        return execution.run();
    }

    /**
     * Returns contextual parsers of a specific type.
     */
    @SuppressWarnings("unchecked")
    public <T extends SocialContextualParser> @NotNull List<T> getContextualParsersByType(@NotNull Class<T> type) {
        final List<T> result = new ArrayList<>();
        for (SocialContextualParser parser : parsers) {
            if (type.isInstance(parser)) {
                result.add((T) parser);
            } else if (parser instanceof SocialParserGroup group) {
                result.addAll(group.getByType(type));
            }
        }
        return List.copyOf(result);
    }

    /**
     * Returns the group containing a parser class, if any.
     */
    public @NotNull Optional<SocialParserGroup> getGroupByContextualParser(
            @NotNull Class<? extends SocialContextualParser> parserClass) {
        return getContextualParsersByType(SocialParserGroup.class).stream()
                .filter(group -> !group.getByType(parserClass).isEmpty())
                .findFirst();
    }

    /**
     * Returns an identified parser by identifier.
     */
    public <T extends SocialIdentifiedParser> @NotNull Optional<T> getIdentifiedContextualParser(@NotNull Class<T> type,
            @NotNull String identifier) {
        return getContextualParsersByType(type).stream()
                .filter(parser -> parser.identifier().equals(identifier))
                .findFirst();
    }

    @NotNull List<SocialContextualParser> getWithExclusions() {
        if (exclusions.isEmpty())
            return parsers;
        return parsers.stream()
                .filter(parser -> !exclusions.contains(parser.getClass()))
                .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextProcessor that)) return false;
        return restrictToPlayerInputParsers == that.restrictToPlayerInputParsers &&
                Objects.equals(parsers, that.parsers) &&
                Objects.equals(exclusions, that.exclusions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parsers, exclusions, restrictToPlayerInputParsers);
    }

    @Override
    public String toString() {
        return "TextProcessor{" +
                "parsersCount=" + parsers.size() +
                ", exclusions=" + exclusions +
                ", restrictToPlayerInputParsers=" + restrictToPlayerInputParsers +
                '}';
    }

    public static class TextProcessorBuilder {
        private List<SocialContextualParser> parsers = new ArrayList<>();
        private Set<Class<? extends SocialContextualParser>> exclusions = new HashSet<>();
        private boolean restrictToPlayerInputParsers = false;

        TextProcessorBuilder() {}

        public @NotNull TextProcessorBuilder parsers(@NotNull List<SocialContextualParser> parsers) {
            this.parsers = new ArrayList<>(parsers);
            return this;
        }

        public @NotNull TextProcessorBuilder exclusions(@NotNull Set<Class<? extends SocialContextualParser>> exclusions) {
            this.exclusions = new HashSet<>(exclusions);
            return this;
        }

        public @NotNull TextProcessorBuilder restrictToPlayerInputParsers(boolean restrictToPlayerInputParsers) {
            this.restrictToPlayerInputParsers = restrictToPlayerInputParsers;
            return this;
        }

        public @NotNull TextProcessor build() {
            return new TextProcessor(parsers, exclusions, restrictToPlayerInputParsers);
        }
    }

}

