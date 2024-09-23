package ovh.mythmc.social.api.placeholders;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.ArrayList;
import java.util.Collection;

import static net.kyori.adventure.text.Component.text;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class SocialPlaceholderProcessor {

    public static final SocialPlaceholderProcessor instance = new SocialPlaceholderProcessor();
    private static final Collection<SocialPlaceholder> placeholders = new ArrayList<>();

    public SocialPlaceholder getPlaceholder(final @NotNull String identifier) {
        for (SocialPlaceholder placeholder : placeholders) {
            if (placeholder.identifier().equals(identifier))
                return placeholder;
        }

        return null;
    }

    public boolean exists(final @NotNull String identifier) {
        return getPlaceholder(identifier) != null;
    }

    public boolean registerPlaceholder(final @NotNull SocialPlaceholder placeholder) {
        return placeholders.add(placeholder);
    }

    public boolean unregisterPlaceholder(final @NotNull SocialPlaceholder placeholder) {
        return placeholders.remove(placeholder);
    }

    public boolean unregisterPlaceholder(final @NotNull String identifier) {
        SocialPlaceholder placeholder = getPlaceholder(identifier);
        if (placeholder == null)
            return false;

        return unregisterPlaceholder(placeholder);
    }

    public Component process(SocialPlayer player, Component component) {
        for (SocialPlaceholder placeholder : placeholders) {
            component = component.replaceText(placeholder.identifier(), text(placeholder.process(player)));
        }

        return component;
    }

    public Component process(SocialPlayer player, String message) {
        Component component = MiniMessage.miniMessage().deserialize(message);
        return process(player, component);
    }

}
