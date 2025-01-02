package ovh.mythmc.social.api.users;

import lombok.*;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;

import java.util.UUID;

import javax.annotation.Nullable;

@Getter
@Setter(AccessLevel.PROTECTED)
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public final class SocialUser {

    private final UUID uuid;

    private ChatChannel mainChannel;

    private boolean muted;

    private boolean socialSpy;

    private long latestMessageInMilliseconds = 0L;

    @Getter(AccessLevel.PRIVATE)
    private String cachedNickname = null;

    public @Nullable Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public String getNickname() {
        if (getPlayer() != null)
            cachedNickname = ChatColor.stripColor(getPlayer().getDisplayName());

        return cachedNickname;
    }

    public void sendMessage(@NonNull SocialParserContext context) {
        if (getPlayer() == null)
            return;
            
        Social.get().getTextProcessor().parseAndSend(context);
    }

    public void sendMessage(@NonNull Component component) {
        SocialParserContext context = SocialParserContext.builder()
            .user(this)
            .message(component)
            .build();

        sendMessage(context);
    }

    public void sendMessage(@NonNull String message) {
        sendMessage(Component.text(message));
    }

}
