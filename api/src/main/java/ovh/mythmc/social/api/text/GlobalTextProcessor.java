package ovh.mythmc.social.api.text;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.keywords.SocialContextualKeyword;
import ovh.mythmc.social.api.text.parsers.SocialContextualParser;
import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;
import ovh.mythmc.social.api.text.parsers.SocialParser;
import ovh.mythmc.social.api.text.parsers.SocialPlaceholder;
import ovh.mythmc.social.api.users.SocialUser;

import static net.kyori.adventure.text.Component.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("deprecation")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class GlobalTextProcessor {

    public static final GlobalTextProcessor instance = new GlobalTextProcessor();

    private final Collection<SocialParser> parsers = new ArrayList<>();

    public final SocialParserGroup EARLY_PARSERS = SocialParserGroup.builder().build();

    public final SocialParserGroup LATE_PARSERS = SocialParserGroup.builder().build();

    @Deprecated
    public SocialPlaceholder getPlaceholder(final @NotNull String identifier) {
        for (SocialParser parser : getParsers()) {
            if (parser instanceof SocialPlaceholder placeholder)
                if (placeholder.identifier().equals(identifier))
                    return placeholder;
        }

        return null;
    }

    public SocialContextualPlaceholder getContextualPlaceholder(final @NotNull String identifier) {
        for (SocialParser parser : getParsers()) {
            if (parser instanceof SocialContextualPlaceholder placeholder)
                if (placeholder.identifier().equals(identifier))
                    return placeholder;
        }

        return null;
    }

    public SocialContextualKeyword getContextualKeyword(final @NotNull String keyword) {
        for (SocialParser parser : getParsers()) {
            if (parser instanceof SocialContextualKeyword contextualKeyword)
                if (contextualKeyword.keyword().equals(keyword))
                    return contextualKeyword;
        }

        return null;
    }

    public boolean isPlaceholder(final @NotNull String identifier) {
        return getPlaceholder(identifier) != null;
    }

    public void registerParser(final @NotNull SocialParser... socialParsers) {
        parsers.addAll(Arrays.asList(socialParsers));
    }

    public void unregisterParser(final @NotNull SocialParser... socialParsers) {
        parsers.removeAll(List.of(socialParsers));
    }

    @Deprecated
    public void unregisterPlaceholder(final @NotNull String identifier) {
        SocialPlaceholder placeholder = getPlaceholder(identifier);
        if (placeholder != null)
            unregisterParser(placeholder);
    }

    public void unregisterAllParsers() {
        EARLY_PARSERS.removeAll();
        parsers.clear();
        LATE_PARSERS.removeAll();
    }

    public List<SocialParser> getParsers() {
        List<SocialParser> parserList = new ArrayList<>();
        parserList.addAll(EARLY_PARSERS.get());
        parserList.addAll(parsers);
        parserList.addAll(LATE_PARSERS.get());
        return parserList;
    }

    public List<SocialContextualParser> getContextualParsers() {
        return getParsers().stream().filter(parser -> parser instanceof SocialContextualParser).map(parser -> (SocialContextualParser) parser).toList();
    }

    public SocialContextualParser getContextualParserByClass(@NotNull Class<?> clazz) {
        return getContextualParsers().stream().filter(parser -> parser.getClass().equals(clazz)).toList().get(0);
    }

    public List<SocialContextualParser> getContextualParsersWithGroupMembers() {
        List<SocialContextualParser> contextualParsers = new ArrayList<>();
        getContextualParsers().stream().forEach(contextualParser -> {
            if (contextualParser instanceof SocialParserGroup group) {
                contextualParsers.addAll(group.get());
                return;
            }

            contextualParsers.add(contextualParser);
        });
        
        return contextualParsers;
    }

    public Component parsePlayerInput(@NotNull SocialParserContext context) {
        CustomTextProcessor textProcessor = CustomTextProcessor.builder()
            .parsers(getParsers())
            .playerInput(true)
            .build();

        return textProcessor.parse(context);
    }

    public Component parse(@NotNull SocialParserContext context) {
        CustomTextProcessor textProcessor = CustomTextProcessor.builder()
            .parsers(getParsers())
            .build();

        return textProcessor.parse(context);
    }

    public Component parse(SocialUser socialPlayer, ChatChannel channel, Component message, ChannelType channelType) {
        return parse(SocialParserContext.builder()
            .user(socialPlayer)
            .channel(channel)
            .message(message)
            .messageChannelType(channelType)
            .build()
        );
    }

    public Component parse(SocialUser socialPlayer, ChatChannel channel, String message, ChannelType channelType) {
        return parse(socialPlayer, channel, text(message), channelType);
    }

    public Component parse(SocialUser socialPlayer, ChatChannel channel, Component message) {
        return parse(socialPlayer, channel, message, ChannelType.CHAT);
    }

    public Component parse(SocialUser socialPlayer, ChatChannel channel, String message) {
        return parse(socialPlayer, channel, text(message));
    }

    public void parseAndSend(SocialParserContext context) {
        send(List.of(context.user()), parse(context), context.messageChannelType());
    }

    public void parseAndSend(SocialUser socialPlayer, ChatChannel chatChannel, Component message, ChannelType channelType) {
        SocialParserContext context = SocialParserContext.builder()
            .user(socialPlayer)
            .channel(chatChannel)
            .message(message)
            .messageChannelType(channelType)
            .build();

        parseAndSend(context);
    }

    public void parseAndSend(SocialUser socialPlayer, ChatChannel chatChannel, String message, ChannelType channelType) {
        parseAndSend(socialPlayer, chatChannel, text(message), channelType);
    }

    public void parseAndSend(SocialUser socialPlayer, ChatChannel chatChannel, Component message) {
        parseAndSend(socialPlayer, chatChannel, message, ChannelType.CHAT);
    }

    public void parseAndSend(SocialUser socialPlayer, ChatChannel chatChannel, String message) {
        parseAndSend(socialPlayer, chatChannel, text(message));
    }

    public void parseAndSend(SocialUser user, Component message, ChannelType type) {
        parseAndSend(user, user.getMainChannel(), message, type);
    }

    public void parseAndSend(SocialUser user, String message, ChannelType type) {
        parseAndSend(user, text(message), type);
    }

    public void send(final @NotNull Collection<SocialUser> members, @NotNull Component message, final @NotNull ChannelType type) {
        if (message == null || message.equals(Component.empty()))
            return;

        members.forEach(socialPlayer -> SocialAdventureProvider.get().sendMessage(socialPlayer, message, type));
    }

    public void send(final @NotNull SocialUser recipient, @NotNull Component message, final @NotNull ChannelType type) {
        send(List.of(recipient), message, type);
    }

}
