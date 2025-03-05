package ovh.mythmc.social.api.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;

public interface SocialUser<P> {

    Optional<P> player();

    Audience audience();

    UUID uuid();

    ChatChannel mainChannel();

    boolean socialSpy();

    List<String> blockedChannels();

    long latestMessageInMilliseconds();

    Style displayNameStyle();

    Optional<SocialUserCompanion> companion();

    Optional<GroupChatChannel> group();

    String name();

    String cachedName();

    void name(@NotNull String name);

    boolean checkPermission(@NotNull String permission);

    default Component displayName() {
        final var displayName = Component.text(cachedName());

        if (displayNameStyle() != null)
            return displayName.style(displayNameStyle());

        return displayName;
    }

    default boolean isOnline() {
        return player().isPresent();
    }
    
}
