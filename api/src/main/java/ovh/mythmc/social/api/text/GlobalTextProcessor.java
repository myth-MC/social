package ovh.mythmc.social.api.text;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.parsers.SocialContextualKeyword;
import ovh.mythmc.social.api.text.parsers.SocialContextualParser;
import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.api.utils.CompanionModUtils;

import static net.kyori.adventure.text.Component.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

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

    public Component parse(SocialUser user, ChatChannel channel, Component message, ChannelType channelType) {
        return parse(SocialParserContext.builder(user, message)
            .channel(channel)
            .messageChannelType(channelType)
            .build()
        );
    }

    public Component parse(SocialUser user, ChatChannel channel, String message, ChannelType channelType) {
        return parse(user, channel, text(message), channelType);
    }

    public Component parse(SocialUser user, ChatChannel channel, Component message) {
        return parse(user, channel, message, ChannelType.CHAT);
    }

    public Component parse(SocialUser user, ChatChannel channel, String message) {
        return parse(user, channel, text(message));
    }

    public void parseAndSend(SocialParserContext context) {
        send(List.of(context.user()), parse(context), context.messageChannelType(), context.channel());
    }

    public void parseAndSend(SocialUser user, ChatChannel chatChannel, Component message, ChannelType channelType) {
        SocialParserContext context = SocialParserContext.builder(user, message)
            .channel(chatChannel)
            .messageChannelType(channelType)
            .build();

        parseAndSend(context);
    }

    public void parseAndSend(SocialUser user, ChatChannel chatChannel, String message, ChannelType channelType) {
        parseAndSend(user, chatChannel, text(message), channelType);
    }

    public void parseAndSend(SocialUser user, ChatChannel chatChannel, Component message) {
        parseAndSend(user, chatChannel, message, ChannelType.CHAT);
    }

    public void parseAndSend(SocialUser user, ChatChannel chatChannel, String message) {
        parseAndSend(user, chatChannel, text(message));
    }

    public void parseAndSend(SocialUser user, Component message, ChannelType type) {
        parseAndSend(user, user.getMainChannel(), message, type);
    }

    public void parseAndSend(SocialUser user, String message, ChannelType type) {
        parseAndSend(user, text(message), type);
    }

    @Internal
    public void send(final @NotNull Collection<SocialUser> members, @NotNull Component message, final @NotNull ChannelType type, final @Nullable ChatChannel channel) {
        if (message == null || message.equals(Component.empty()))
            return;

        switch (type) {
            case ACTION_BAR -> members.forEach(user -> user.sendActionBar(message));
            case CHAT -> {
                members.forEach(user -> {
                    Component userMessage = message;

                    if (user.isCompanion()) {
                        if (channel == null) {
                            userMessage = CompanionModUtils.asBroadcast(message);
                        } else {
                            userMessage = CompanionModUtils.asChannelable(message, channel);
                        }
                    }

                    user.sendMessage(userMessage);
                });
            }
        }
    }

    public void send(final @NotNull SocialUser recipient, @NotNull Component message, final @NotNull ChannelType type, final @Nullable ChatChannel channel) {
        send(List.of(recipient), message, type, channel);
    }

}
