package ovh.mythmc.social.api.text;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.filters.SocialFilterLike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class SocialTextProcessor {

    public static final SocialTextProcessor instance = new SocialTextProcessor();
    private static final Collection<SocialParser> parsers = new ArrayList<>();

    public SocialPlaceholder getPlaceholder(final @NotNull String identifier) {
        for (SocialParser parser : parsers) {
            if (parser instanceof SocialPlaceholder placeholder)
                if (placeholder.identifier().equals(identifier))
                    return placeholder;
        }

        return null;
    }

    public Collection<SocialParser> getParsers() {
        return parsers;
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

    public Component process(SocialPlayer player, Component component) {
        for (SocialParser parser : parsers) {
            component = parser.parse(player, component);
        }

        return component;
    }

    public Component process(SocialPlayer player, String message) {
        Component component = MiniMessage.miniMessage().deserialize(message);
        return process(player, component);
    }

    public void processAndSend(SocialPlayer player, Component component, ChannelType type) {
        send(List.of(player), process(player, component), type);
    }

    public void processAndSend(SocialPlayer player, String message, ChannelType type) {
        processAndSend(player, process(player, message), type);
    }

    public void processAndSend(SocialPlayer player, Component component) {
        processAndSend(player, component, ChannelType.CHAT);
    }

    public void processAndSend(SocialPlayer player, String message) {
        processAndSend(player, message, ChannelType.CHAT);
    }

    public void send(final @NotNull Collection<SocialPlayer> members,
                     @NotNull Component message,
                     final @NotNull ChannelType type) {

        for (SocialPlayer player : members) {
            for (SocialParser parser : parsers) {
                if (parser instanceof SocialFilterLike)
                    message = parser.parse(player, message);
            }

            SocialAdventureProvider.get().sendMessage(player, message, type);
        }
    }

}
