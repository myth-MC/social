package ovh.mythmc.social.api.text;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.handlers.RegisteredMessageHandler;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.parsers.SocialContextualKeyword;
import ovh.mythmc.social.api.text.parsers.SocialContextualParser;
import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;
import ovh.mythmc.social.api.users.SocialUser;

import static net.kyori.adventure.text.Component.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GlobalTextProcessor {

    public static final GlobalTextProcessor instance = new GlobalTextProcessor();

    private final Collection<SocialContextualParser> parsers = new ArrayList<>();

    public final SocialParserGroup EARLY_PARSERS = SocialParserGroup.builder().build();

    public final SocialParserGroup LATE_PARSERS = SocialParserGroup.builder().build();

    public Optional<SocialContextualPlaceholder> getContextualPlaceholder(final @NotNull String identifier) {
        return getContextualParsers().stream()
            .filter(parser -> parser instanceof SocialContextualPlaceholder placeholder && placeholder.identifier().equals(identifier))
            .map(parser -> (SocialContextualPlaceholder) parser)
            .findFirst();
    }

    public Optional<SocialContextualKeyword> getContextualKeyword(final @NotNull String identifier) {
        return getContextualParsers().stream()
            .filter(parser -> parser instanceof SocialContextualKeyword keyword && keyword.keyword().equals(identifier))
            .map(parser -> (SocialContextualKeyword) parser)
            .findFirst();
    }

    public boolean isContextualPlaceholder(final @NotNull String identifier) {
        return getContextualPlaceholder(identifier) != null;
    }

    public void registerContextualParser(final @NotNull SocialContextualParser... socialParsers) {
        parsers.addAll(Arrays.asList(socialParsers));
    }

    public void unregisterContextualParser(final @NotNull SocialContextualParser... socialParsers) {
        parsers.removeAll(List.of(socialParsers));
    }

    public void unregisterAllParsers() {
        EARLY_PARSERS.removeAll();
        parsers.clear();
        LATE_PARSERS.removeAll();
    }

    public void unregisterContextualPlaceholder(final @NotNull String identifier) {
        getContextualPlaceholder(identifier).ifPresent(placeholder -> unregisterContextualParser(placeholder));
    }

    public void unregisterContextualKeyword(final @NotNull String identifier) {
        getContextualKeyword(identifier).ifPresent(keyword -> unregisterContextualParser(keyword));
    }

    public List<SocialContextualParser> getContextualParsers() {
        List<SocialContextualParser> parserList = new ArrayList<>();
        parserList.addAll(EARLY_PARSERS.get());
        parserList.addAll(parsers);
        parserList.addAll(LATE_PARSERS.get());
        return parserList;
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

    public Component parse(SocialUser user, ChatChannel channel, Component message) {
        return parse(SocialParserContext.builder()
            .user(user)
            .channel(channel)
            .message(message)
            .build()
        );
    }

    public Component parse(SocialUser user, ChatChannel channel, String message) {
        return parse(user, channel, text(message));
    }

    public void parseAndSend(SocialParserContext context) {
        send(List.of(context.user()), context);
    }

    public void parseAndSend(SocialUser user, ChatChannel chatChannel, Component message) {
        SocialParserContext context = SocialParserContext.builder()
            .user(user)
            .channel(chatChannel)
            .message(message)
            .build();

        parseAndSend(context);
    }

    public void parseAndSend(SocialUser user, ChatChannel chatChannel, String message) {
        parseAndSend(user, chatChannel, text(message));
    }

    public void parseAndSend(SocialUser user, Component message) {
        parseAndSend(user, user.getMainChannel(), message);
    }

    public void parseAndSend(SocialUser user, String message) {
        parseAndSend(user, text(message));
    }

    public void parseAndSendAsSystemMessage(SocialUser recipient, ChatChannel channel, Component message) {
        SocialParserContext context = SocialParserContext.builder()
            .user(recipient)
            .channel(channel)
            .handler(RegisteredMessageHandler.Default.SYSTEM)
            .build();

        send(recipient, context);
    }

    public void parseAndSendAsSystemMessage(SocialUser recipient, ChatChannel channel, String message) {
        parseAndSendAsSystemMessage(recipient, channel, text(message));
    }

    @Deprecated(forRemoval = true)
    public void send(final @NotNull Collection<SocialUser> members, @NotNull SocialParserContext context) {
        members.forEach(member -> send(member, context));
    }

    @Deprecated(forRemoval = true)
    public void send(final @NotNull SocialUser recipient, @NotNull SocialParserContext context) {
        Social.get().getMessageHandlerRegistry().handle(recipient, context);
    }

}
