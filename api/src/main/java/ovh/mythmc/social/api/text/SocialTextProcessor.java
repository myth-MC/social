package ovh.mythmc.social.api.text;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.annotations.SocialParserProperties;
import ovh.mythmc.social.api.text.filters.SocialFilterLike;
import ovh.mythmc.social.api.text.parsers.SocialParser;
import ovh.mythmc.social.api.text.parsers.SocialPlaceholder;
import ovh.mythmc.social.api.text.parsers.SocialPlayerInputParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class SocialTextProcessor {

    public static final SocialTextProcessor instance = new SocialTextProcessor();

    private final Collection<SocialParser> parsers = new ArrayList<>();

    public SocialPlaceholder getPlaceholder(final @NotNull String identifier) {
        for (SocialParser parser : parsers) {
            if (parser instanceof SocialPlaceholder placeholder)
                if (placeholder.identifier().equals(identifier))
                    return placeholder;
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

    public void unregisterPlaceholder(final @NotNull String identifier) {
        SocialPlaceholder placeholder = getPlaceholder(identifier);
        if (placeholder != null)
            unregisterParser(placeholder);
    }

    public Component parsePlayerInput(SocialPlayer sender, Component message) {
        for (SocialParser parser : parsers) {
            if (parser instanceof SocialPlayerInputParser)
                message = parser.parse(sender, message);
        }

        return message;
    }

    public Component parsePlayerInput(SocialPlayer sender, String message) {
        return parsePlayerInput(sender, Component.text(message));
    }

    private List<SocialParser> getByPriority(final @NotNull SocialParserProperties.ParserPriority priority) {
        List<SocialParser> socialParserList = new ArrayList<>();
        for (SocialParser parser : parsers) {
            SocialParserProperties.ParserPriority parserPriority = SocialParserProperties.ParserPriority.NORMAL;
            if (parser.getClass().isAnnotationPresent(SocialParserProperties.class))
                parserPriority = parser.getClass().getAnnotation(SocialParserProperties.class).priority();

            if (parserPriority.equals(priority))
                socialParserList.add(parser);
        }

        return socialParserList;
    }

    public Component parse(SocialPlayer player, Component component) {
        for (SocialParser parser : getByPriority(SocialParserProperties.ParserPriority.HIGH)) {
            if (parser instanceof SocialFilterLike)
                continue;

            component = parser.parse(player, component);
        }

        for (SocialParser parser : getByPriority(SocialParserProperties.ParserPriority.NORMAL)) {
            if (parser instanceof SocialFilterLike)
                continue;

            component = parser.parse(player, component);
        }

        for (SocialParser parser : getByPriority(SocialParserProperties.ParserPriority.LOW)) {
            if (parser instanceof SocialFilterLike)
                continue;

            component = parser.parse(player, component);
        }

        return component;
    }

    public Component parse(SocialPlayer player, String message) {
        //Component component = MiniMessage.miniMessage().deserialize(message);
        return parse(player, Component.text(message));
    }

    public void parseAndSend(SocialPlayer player, Component component, ChannelType type) {
        send(List.of(player), parse(player, component), type);
    }

    public void parseAndSend(SocialPlayer player, String message, ChannelType type) {
        parseAndSend(player, parse(player, message), type);
    }

    public void parseAndSend(CommandSender commandSender, Component message, ChannelType type) {
        SocialPlayer socialPlayer = null;

        if (commandSender instanceof Player player)
            socialPlayer = Social.get().getPlayerManager().get(player.getUniqueId());

        if (socialPlayer == null) {
            sendToConsole(commandSender, message);
            return;
        }

        parseAndSend(socialPlayer, message, type);
    }

    public void parseAndSend(CommandSender commandSender, String message, ChannelType type) {
        //Component component = MiniMessage.miniMessage().deserialize(message);
        parseAndSend(commandSender, Component.text(message), type);
    }

    @ApiStatus.Experimental
    public void sendToConsole(final @NotNull CommandSender commandSender,
                              @NotNull Component message) {
        // Todo: parse?
        SocialAdventureProvider.get().sender(commandSender).sendMessage(message);
    }

    public void send(final @NotNull SocialPlayer recipient,
                     @NotNull Component message,
                     final @NotNull ChannelType type) {
        send(List.of(recipient), message, type);
    }

    public void send(final @NotNull Collection<SocialPlayer> members,
                     @NotNull Component message,
                     final @NotNull ChannelType type) {

        members.forEach(socialPlayer -> SocialAdventureProvider.get().sendMessage(socialPlayer, message, type));
    }

}
