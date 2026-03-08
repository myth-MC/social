package ovh.mythmc.social.api.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.parser.SocialContextualKeyword;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;
import ovh.mythmc.social.api.text.parser.SocialIdentifiedParser;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.api.util.CompanionModUtils;

import static net.kyori.adventure.text.Component.text;

/**
 * The global entry-point for parsing and sending messages through the social
 * plugin.
 *
 * <p>
 * Parsers are divided into three ordered groups:
 * <ol>
 * <li>{@link #EARLY_PARSERS} run before the main parser list</li>
 * <li>The main parser list registered via
 * {@link #registerContextualParser}</li>
 * <li>{@link #LATE_PARSERS} run after the main parser list</li>
 * </ol>
 *
 * <p>
 * Access the singleton via
 * {@link ovh.mythmc.social.api.Social#getTextProcessor()}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GlobalTextProcessor {

    public static final GlobalTextProcessor instance = new GlobalTextProcessor();

    private final List<SocialContextualParser> parsers = new ArrayList<>();

    // Cached view of [EARLY_PARSERS, ...parsers, LATE_PARSERS]; rebuilt only when
    // parsers are registered/unregistered to avoid a per-call ArrayList allocation.
    private volatile List<SocialContextualParser> cachedContextualParsers = null;

    public final SocialParserGroup EARLY_PARSERS = SocialParserGroup.builder().build();
    public final SocialParserGroup LATE_PARSERS = SocialParserGroup.builder().build();

    // -- parser list --

    /**
     * Returns the ordered list of all contextual parsers: {@link #EARLY_PARSERS},
     * then the main parser list, then {@link #LATE_PARSERS}.
     *
     * <p>
     * The list is cached and only rebuilt when parsers are registered or
     * unregistered.
     *
     * @return the full parser list
     */
    public List<SocialContextualParser> getContextualParsers() {
        if (cachedContextualParsers == null)
            cachedContextualParsers = buildContextualParsers();
        return cachedContextualParsers;
    }

    private List<SocialContextualParser> buildContextualParsers() {
        final List<SocialContextualParser> list = new ArrayList<>(parsers.size() + 2);
        list.add(EARLY_PARSERS);
        list.addAll(parsers);
        list.add(LATE_PARSERS);
        return Collections.unmodifiableList(list);
    }

    private void invalidateCache() {
        cachedContextualParsers = null;
    }

    // -- lookups --

    /**
     * Returns all parsers of the given type, including those nested inside
     * {@link SocialParserGroup} instances.
     *
     * @param type the parser type to search for
     * @param <T>  the type parameter
     * @return an unordered collection of matching parsers
     */
    @SuppressWarnings("unchecked")
    public <T extends SocialContextualParser> Collection<T> getContextualParsersByType(Class<T> type) {
        final List<T> result = new ArrayList<>();
        for (SocialContextualParser parser : getContextualParsers()) {
            if (type.isInstance(parser)) {
                result.add((T) parser);
            } else if (parser instanceof SocialParserGroup group) {
                result.addAll(group.getByType(type));
            }
        }
        return result;
    }

    /**
     * Returns the {@link SocialParserGroup} that contains the given parser class,
     * if any.
     *
     * @param parserClass the parser class to look for
     * @return an optional containing the group
     */
    public Optional<SocialParserGroup> getGroupByContextualParser(
            @NotNull Class<? extends SocialContextualParser> parserClass) {
        return getContextualParsersByType(SocialParserGroup.class).stream()
                .filter(group -> !group.getByType(parserClass).isEmpty())
                .findFirst();
    }

    /**
     * Returns the parser of the given type whose
     * {@link SocialIdentifiedParser#identifier()}
     * matches the given string, if any.
     *
     * @param type       the parser type
     * @param identifier the identifier to match
     * @param <T>        the type parameter
     * @return an optional containing the matching parser
     */
    public <T extends SocialIdentifiedParser> Optional<T> getIdentifiedParser(@NotNull Class<T> type,
            @NotNull String identifier) {
        return getContextualParsersByType(type).stream()
                .filter(parser -> parser.identifier().equals(identifier))
                .findFirst();
    }

    // -- registration --

    /**
     * Registers one or more contextual parsers into the main parser list.
     *
     * @param socialParsers the parsers to register
     */
    public void registerContextualParser(@NotNull SocialContextualParser... socialParsers) {
        parsers.addAll(Arrays.asList(socialParsers));
        invalidateCache();
    }

    /**
     * Creates and registers a {@link SocialContextualPlaceholder} with the given
     * identifier.
     *
     * <p>
     * The placeholder replaces its matched token with the component returned by
     * {@code ctx}.
     *
     * @param identifier the unique placeholder identifier
     * @param ctx        a function that produces the replacement component from the
     *                   parse context
     */
    public void registerContextualPlaceholder(@NotNull String identifier,
            @NotNull Function<SocialParserContext, Component> ctx) {
        registerContextualParser(new SocialContextualPlaceholder() {
            @Override
            public String identifier() {
                return identifier;
            }

            @Override
            public Component get(SocialParserContext context) {
                return ctx.apply(context);
            }
        });
    }

    /**
     * Creates and registers a {@link SocialContextualKeyword} with the given
     * identifier.
     *
     * <p>
     * The keyword processes its matched token and returns a replacement component
     * produced by {@code ctx}.
     *
     * @param identifier the keyword string
     * @param ctx        a function that produces the replacement component from the
     *                   parse context
     */
    public void registerContextualKeyword(@NotNull String identifier,
            @NotNull Function<SocialParserContext, Component> ctx) {
        registerContextualParser(new SocialContextualKeyword() {
            @Override
            public String keyword() {
                return identifier;
            }

            @Override
            public Component process(SocialParserContext context) {
                return ctx.apply(context);
            }
        });
    }

    // -- unregistration --

    /**
     * Unregisters all parsers from all groups and the main parser list.
     */
    @Internal
    public void unregisterAllParsers() {
        EARLY_PARSERS.removeAll();
        parsers.clear();
        LATE_PARSERS.removeAll();
        invalidateCache();
    }

    /**
     * Removes the given parsers from the main parser list.
     *
     * @param socialParsers the parsers to remove
     */
    public void unregisterContextualParser(@NotNull SocialContextualParser... socialParsers) {
        parsers.removeAll(List.of(socialParsers));
        invalidateCache();
    }

    public void unregisterContextualPlaceholder(@NotNull String identifier) {
        getContextualPlaceholder(identifier).ifPresent(this::unregisterContextualParser);
    }

    public void unregisterContextualKeyword(@NotNull String identifier) {
        getContextualKeyword(identifier).ifPresent(this::unregisterContextualParser);
    }

    // -- parsing --

    /**
     * Parses {@code context} treating its message as raw player input, applying
     * only
     * {@link ovh.mythmc.social.api.text.parser.SocialUserInputParser} parsers.
     *
     * @param context the parse context containing the user and raw message
     * @return the parsed component
     */
    public Component parsePlayerInput(@NotNull SocialParserContext context) {
        return TextProcessor.builder()
                .parsers(getContextualParsers())
                .restrictToPlayerInputParsers(true)
                .build()
                .parse(context);
    }

    /**
     * Parses {@code context} through the full parser chain and returns the
     * resulting component.
     *
     * @param context the parse context
     * @return the parsed component
     */
    public Component parse(@NotNull SocialParserContext context) {
        return TextProcessor.builder()
                .parsers(getContextualParsers())
                .build()
                .parse(context);
    }

    /**
     * Parses a message component for the given user/channel combination.
     *
     * @param user        the sending user
     * @param channel     the target channel
     * @param message     the message component
     * @param channelType how the message will be delivered
     * @return the parsed component
     */
    public Component parse(SocialUser user, ChatChannel channel, Component message,
            ChatChannel.ChannelType channelType) {
        return parse(SocialParserContext.builder(user, message)
                .channel(channel)
                .messageChannelType(channelType)
                .build());
    }

    public Component parse(SocialUser user, ChatChannel channel, String message,
            ChatChannel.ChannelType channelType) {
        return parse(user, channel, text(message), channelType);
    }

    public Component parse(SocialUser user, ChatChannel channel, Component message) {
        return parse(user, channel, message, ChatChannel.ChannelType.CHAT);
    }

    public Component parse(SocialUser user, ChatChannel channel, String message) {
        return parse(user, channel, text(message));
    }

    // -- send --

    /**
     * Parses {@code context} and immediately sends the result to the context's
     * user.
     *
     * @param context the pre-built parse context
     */
    public void parseAndSend(SocialParserContext context) {
        send(List.of(context.user()), parse(context), context.messageChannelType(), context.channel());
    }

    public void parseAndSend(SocialUser user, ChatChannel channel, Component message,
            ChatChannel.ChannelType channelType) {
        parseAndSend(SocialParserContext.builder(user, message)
                .channel(channel)
                .messageChannelType(channelType)
                .build());
    }

    public void parseAndSend(SocialUser user, ChatChannel channel, String message,
            ChatChannel.ChannelType channelType) {
        parseAndSend(user, channel, text(message), channelType);
    }

    public void parseAndSend(SocialUser user, ChatChannel channel, Component message) {
        parseAndSend(user, channel, message, ChatChannel.ChannelType.CHAT);
    }

    public void parseAndSend(SocialUser user, ChatChannel channel, String message) {
        parseAndSend(user, channel, text(message));
    }

    public void parseAndSend(SocialUser user, Component message, ChatChannel.ChannelType type) {
        parseAndSend(user, user.mainChannel().get(), message, type);
    }

    public void parseAndSend(SocialUser user, String message, ChatChannel.ChannelType type) {
        parseAndSend(user, text(message), type);
    }

    /**
     * Sends a pre-parsed {@code message} to the given collection of users.
     *
     * @param members the recipients
     * @param message the pre-parsed component to send
     * @param type    how the message should be delivered
     * @param channel the originating channel, or {@code null} for non-channel
     *                messages
     */
    @Internal
    public void send(@NotNull Collection<SocialUser> members, @NotNull Component message,
            @NotNull ChatChannel.ChannelType type, @Nullable ChatChannel channel) {
        if (message.equals(Component.empty()))
            return;

        switch (type) {
            case ACTION_BAR -> members.forEach(user -> user.audience().sendActionBar(message));
            case CHAT -> members.forEach(user -> {
                final Component userMessage = user.companion().isPresent()
                        ? (channel == null ? CompanionModUtils.asBroadcast(message)
                                : CompanionModUtils.asChannelable(message, channel))
                        : message;
                user.audience().sendMessage(userMessage);
            });
        }
    }

    /**
     * Sends a pre-parsed {@code message} to a single recipient.
     *
     * @param recipient the user to send to
     * @param message   the pre-parsed component
     * @param type      how the message should be delivered
     * @param channel   the originating channel, or {@code null}
     */
    @Internal
    public void send(@NotNull SocialUser recipient, @NotNull Component message,
            @NotNull ChatChannel.ChannelType type, @Nullable ChatChannel channel) {
        send(List.of(recipient), message, type, channel);
    }

    // -- deprecated --

    /**
     * Gets a {@link SocialContextualParser}s matching a specific class.
     * @param clazz the class to match
     * @return      a {@link SocialContextualParser} matching the given class, or
     *              {@code null} otherwise
     * @deprecated  Use {@link getIdentifiedParser(type, identifier)} instead
     */
    @Deprecated(forRemoval = true)
    public SocialContextualParser getContextualParserByClass(@NotNull Class<?> clazz) {
        return getContextualParsersWithGroupMembers().stream()
                .filter(parser -> parser.getClass().equals(clazz))
                .findFirst()
                .orElse(null);
    }

    @Deprecated(forRemoval = true)
    public Collection<SocialContextualParser> getContextualParsersWithGroupMembers() {
        final List<SocialContextualParser> result = new ArrayList<>();
        for (SocialContextualParser parser : getContextualParsers()) {
            if (parser instanceof SocialParserGroup group) {
                result.addAll(group.get());
            } else {
                result.add(parser);
            }
        }
        return result;
    }

    /**
     * Gets a specific {@link SocialContextualPlaceholder} by its identifier.
     * @param identifier the identifier of the {@link SocialContextualPlaceholder}
     * @return           a {@link Optional} wrapping the {@link SocialContextualPlaceholder} if
     *                   available, or an empty {@link Optional} otherwise
     */
    @Deprecated(forRemoval = true)
    public Optional<SocialContextualPlaceholder> getContextualPlaceholder(@NotNull String identifier) {
        return getIdentifiedParser(SocialContextualPlaceholder.class, identifier);
    }

    /**
     * Gets a specific {@link SocialContextualKeyword} by its identifier.
     * @param identifier the identifier of the {@link SocialContextualKeyword}
     * @return           a {@link Optional} wrapping the {@link SocialContextualKeyword} if
     *                   available, or an empty {@link Optional} otherwise
     * @deprecated       Use {@link getIdentifiedParser(type, identifier)} instead
     */
    @Deprecated(forRemoval = true)
    public Optional<SocialContextualKeyword> getContextualKeyword(@NotNull String identifier) {
        return getIdentifiedParser(SocialContextualKeyword.class, identifier);
    }

    /**
     * Determines whether the text processor has a {@link SocialContextualPlaceholder}
     * matching the given {@code identifier}.
     * @param identifier the identifier of the {@link SocialContextualPlaceholder} to check
     * @return           {@code true} if the {@link SocialContextualPlaceholder} exists,
     *                   {@code false} otherwise
     * @deprecated       Use {@link getIdentifiedParser(type, identifier)} instead
     */
    @Deprecated(forRemoval = true)
    public boolean isContextualPlaceholder(@NotNull String identifier) {
        return getContextualPlaceholder(identifier).isPresent();
    }

    /**
     * Determines whether the text processor has a {@link SocialContextualKeyword}
     * matching the given {@code identifier}.
     * @param keyword the identifier of the {@link SocialContextualKeyword} to check
     * @return        {@code true} if the {@link SocialContextualKeyword} exists,
     *                {@code false} otherwise
     * @deprecated    Use {@link getIdentifiedParser(type, identifier)} instead
     */
    @Deprecated(forRemoval = true)
    public boolean isContextualKeyword(@NotNull String keyword) {
        return getContextualKeyword(keyword).isPresent();
    }

}
