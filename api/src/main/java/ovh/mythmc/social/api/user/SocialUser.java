package ovh.mythmc.social.api.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.util.Mutable;

public interface SocialUser {

    @NotNull Class<? extends SocialUser> rendererClass();

    @NotNull Audience audience();

    @NotNull UUID uuid();

    @Nullable ChatChannel mainChannel();

    @NotNull Mutable<Boolean> socialSpy();

    @NotNull List<String> blockedChannels();

    @NotNull Mutable<Long> latestMessageInMilliseconds();

    @NotNull Mutable<Style> displayNameStyle();

    @NotNull Optional<SocialUserCompanion> companion();

    @NotNull Optional<GroupChatChannel> group();

    @NotNull String name();

    @NotNull Mutable<String> cachedDisplayName();

    void name(@NotNull String name);

    boolean checkPermission(@NotNull String permission);

    boolean isOnline();

    default @NotNull TextComponent displayName() {
        final var displayName = Component.text(cachedDisplayName().get());

        if (displayNameStyle().isPresent())
            return displayName.style(displayNameStyle().get());

        return displayName;
    }
    
}
