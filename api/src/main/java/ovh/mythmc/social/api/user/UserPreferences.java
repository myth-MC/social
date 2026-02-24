package ovh.mythmc.social.api.user;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.TextComponent;
import ovh.mythmc.social.api.util.Mutable;

public interface UserPreferences {

    @NotNull
    Mutable<TextComponent> displayName();

    @NotNull
    Mutable<Boolean> socialSpy();

    @NotNull
    Mutable<Long> lastMessageTimestamp();

}
