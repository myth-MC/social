package ovh.mythmc.social.api.text;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.parser.SocialContextualKeyword;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;
import ovh.mythmc.social.api.text.parser.SocialIdentifiedParser;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.util.CompanionModUtils;

import static net.kyori.adventure.text.Component.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GlobalTextProcessor {

    public static final GlobalTextProcessor instance = new GlobalTextProcessor();

    private final Collection<SocialContextualParser> parsers = new ArrayList<>();

    public final SocialParserGroup EARLY_PARSERS = SocialParserGroup.builder().build();

    public final SocialParserGroup LATE_PARSERS = SocialParserGroup.builder().build();

    public List<SocialContextualParser> getContextualParsers() {
        List<SocialContextualParser> parserList = new ArrayList<>();
        parserList.add(EARLY_PARSERS);
        parserList.addAll(parsers);
        parserList.add(LATE_PARSERS);
        return parserList;
    }

    @Deprecated(forRemoval = true)
    public SocialContextualParser getContextualParserByClass(@NotNull Class<?> clazz) {
        return getContextualParsersWithGroupMembers().stream().filter(parser -> parser.getClass().equals(clazz)).findFirst().orElse(null);
    }

    @Deprecated(forRemoval = true)
    public Collection<SocialContextualParser> getContextualParsersWithGroupMembers() {
        List<SocialContextualParser> contextualParsers = new ArrayList<>();
        getContextualParsers().forEach(contextualParser -> {
            if (contextualParser instanceof SocialParserGroup group) {
                contextualParsers.addAll(group.get());
                return;
            }

            contextualParsers.add(contextualParser);
        });
        
        return contextualParsers;
    }

    public <T extends SocialContextualParser> Collection<T> getContextualParsersByType(Class<T> type) {
        final var textProcessor = CustomTextProcessor.builder()
            .parsers(getContextualParsers())
            .build();

        return textProcessor.getContextualParsersByType(type);
    }

    public Optional<SocialParserGroup> getGroupByContextualParser(final @NotNull Class<? extends SocialContextualParser> parserClass) {
        return getContextualParsersByType(SocialParserGroup.class).stream()
            .filter(group -> !group.getByType(parserClass).isEmpty())
            .findFirst();
    }

    public <T extends SocialIdentifiedParser> Optional<T> getIdentifiedParser(final @NotNull Class<T> type, final @NotNull String identifier) {
        return getContextualParsersByType(type).stream()
            .filter(parser -> parser.identifier().equals(identifier))
            .findFirst();
    }

    @Deprecated(forRemoval = true)
    public Optional<SocialContextualPlaceholder> getContextualPlaceholder(final @NotNull String identifier) {
        return getIdentifiedParser(SocialContextualPlaceholder.class, identifier);
    }

    @Deprecated(forRemoval = true)
    public Optional<SocialContextualKeyword> getContextualKeyword(final @NotNull String identifier) {
        return getIdentifiedParser(SocialContextualKeyword.class, identifier);
    }

    @Deprecated(forRemoval = true)
    public boolean isContextualPlaceholder(final @NotNull String identifier) {
        return getContextualPlaceholder(identifier) != null;
    }

    @Deprecated(forRemoval = true)
    public boolean isContextualKeyword(final @NotNull String keyword) {
        return getContextualKeyword(keyword) != null;
    }

    public void registerContextualParser(final @NotNull SocialContextualParser... socialParsers) {
        parsers.addAll(Arrays.asList(socialParsers));
    }

    public void registerContextualPlaceholder(final @NotNull String identifier, final @NotNull Function<SocialParserContext, Component> ctx) {
        var placeholder = new SocialContextualPlaceholder() {

            @Override
            public String identifier() {
                return identifier;
            }

            @Override
            public Component get(SocialParserContext context) {
                return ctx.apply(context);
            }
            
        };

        registerContextualParser(placeholder);
    }

    public void registerContextualKeyword(final @NotNull String identifier, final @NotNull Function<SocialParserContext, Component> ctx) {
        var keyword = new SocialContextualKeyword() {

            @Override
            public String keyword() {
                return identifier;
            }

            @Override
            public Component process(SocialParserContext context) {
                return ctx.apply(context);
            }
            
        };

        registerContextualParser(keyword);
    }

    @Internal
    public void unregisterAllParsers() {
        EARLY_PARSERS.removeAll();
        parsers.clear();
        LATE_PARSERS.removeAll();
    }

    public void unregisterContextualParser(final @NotNull SocialContextualParser... socialParsers) {
        parsers.removeAll(List.of(socialParsers));
    }

    public void unregisterContextualPlaceholder(final @NotNull String identifier) {
        getContextualPlaceholder(identifier).ifPresent(this::unregisterContextualParser);
    }

    public void unregisterContextualKeyword(final @NotNull String identifier) {
        getContextualKeyword(identifier).ifPresent(this::unregisterContextualParser);
    }

    public Component parsePlayerInput(@NotNull SocialParserContext context) {
        CustomTextProcessor textProcessor = CustomTextProcessor.builder()
            .parsers(getContextualParsers())
            .playerInput(true)
            .build();

        return textProcessor.parse(context);
    }

    public Component parse(@NotNull SocialParserContext context) {
        CustomTextProcessor textProcessor = CustomTextProcessor.builder()
            .parsers(getContextualParsers())
            .build();

        return textProcessor.parse(context);
    }

    public Component parse(AbstractSocialUser user, ChatChannel channel, Component message, ChannelType channelType) {
        return parse(SocialParserContext.builder(user, message)
            .channel(channel)
            .messageChannelType(channelType)
            .build()
        );
    }

    public Component parse(AbstractSocialUser user, ChatChannel channel, String message, ChannelType channelType) {
        return parse(user, channel, text(message), channelType);
    }

    public Component parse(AbstractSocialUser user, ChatChannel channel, Component message) {
        return parse(user, channel, message, ChannelType.CHAT);
    }

    public Component parse(AbstractSocialUser user, ChatChannel channel, String message) {
        return parse(user, channel, text(message));
    }

    public void parseAndSend(SocialParserContext context) {
        send(List.of(context.user()), parse(context), context.messageChannelType(), context.channel());
    }

    public void parseAndSend(AbstractSocialUser user, ChatChannel ChatChannel, Component message, ChannelType channelType) {
        SocialParserContext context = SocialParserContext.builder(user, message)
            .channel(ChatChannel)
            .messageChannelType(channelType)
            .build();

        parseAndSend(context);
    }

    public void parseAndSend(AbstractSocialUser user, ChatChannel ChatChannel, String message, ChannelType channelType) {
        parseAndSend(user, ChatChannel, text(message), channelType);
    }

    public void parseAndSend(AbstractSocialUser user, ChatChannel ChatChannel, Component message) {
        parseAndSend(user, ChatChannel, message, ChannelType.CHAT);
    }

    public void parseAndSend(AbstractSocialUser user, ChatChannel ChatChannel, String message) {
        parseAndSend(user, ChatChannel, text(message));
    }

    public void parseAndSend(AbstractSocialUser user, Component message, ChannelType type) {
        parseAndSend(user, user.mainChannel(), message, type);
    }

    public void parseAndSend(AbstractSocialUser user, String message, ChannelType type) {
        parseAndSend(user, text(message), type);
    }

    @Internal
    public void send(final @NotNull Collection<AbstractSocialUser> members, @NotNull Component message, final @NotNull ChannelType type, final @Nullable ChatChannel channel) {
        if (message.equals(Component.empty()))
            return;

        switch (type) {
            case ACTION_BAR -> members.forEach(user -> user.audience().sendActionBar(message));
            case CHAT -> members.forEach(user -> {
                Component userMessage = message;

                if (user.companion().isPresent()) {
                    if (channel == null) {
                        userMessage = CompanionModUtils.asBroadcast(message);
                    } else {
                        userMessage = CompanionModUtils.asChannelable(message, channel);
                    }
                }

                user.audience().sendMessage(userMessage);
            });
        }
    }

    @Internal
    public void send(final @NotNull AbstractSocialUser recipient, @NotNull Component message, final @NotNull ChannelType type, final @Nullable ChatChannel channel) {
        send(List.of(recipient), message, type, channel);
    }

}
