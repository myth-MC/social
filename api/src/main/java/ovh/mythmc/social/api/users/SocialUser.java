package ovh.mythmc.social.api.users;

import lombok.*;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.database.model.IgnoredUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import javax.annotation.Nullable;

@Getter
@Setter(AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@DatabaseTable(tableName = "users")
public class SocialUser implements SocialUserAudienceWrapper {

    public static class Dummy extends SocialUser {

        public Dummy(ChatChannel channel) {
            super(UUID.nameUUIDFromBytes("#Dummy".getBytes()), channel, false, null, new ArrayList<>(), 0, "Dummy");
        }

        public Dummy() {
            super(UUID.nameUUIDFromBytes("#Dummy".getBytes()), Social.get().getChatManager().getDefaultChannel(), false, null, new ArrayList<>(), 0, "Dummy");
        }

    }

    @DatabaseField(id = true)
    private @NotNull UUID uuid;

    private ChatChannel mainChannel = Social.get().getChatManager().getDefaultChannel();

    private boolean socialSpy = false;

    @Getter(AccessLevel.PROTECTED)
    @ForeignCollectionField(eager = true)
    private ForeignCollection<IgnoredUser> ignoredUsers;

    @Getter(AccessLevel.PROTECTED)
    private Collection<String> blockedChannels = new ArrayList<>();

    private long latestMessageInMilliseconds = 0L;

    @DatabaseField
    @Getter(AccessLevel.PRIVATE)
    private String cachedNickname = null;

    public @Nullable Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public @Nullable CommandSender asCommandSender() {
        if (getPlayer() != null)
            return getPlayer();

        return Bukkit.getConsoleSender();
    }

    public @Nullable GroupChatChannel getGroupChatChannel() {
        return Social.get().getChatManager().getGroupChannelByUser(this);
    }

    public String getNickname() {
        if (getPlayer() != null)
            cachedNickname = ChatColor.stripColor(getPlayer().getDisplayName());

        return cachedNickname;
    }

    // Send social messages
    public void sendParsableMessage(@NonNull SocialParserContext context, boolean playerInput) {
        if (getPlayer() == null)
            return;
        
        Component parsedMessage = null;

        if (playerInput) {
            parsedMessage = Social.get().getTextProcessor().parsePlayerInput(context);
        } else {
            parsedMessage = Social.get().getTextProcessor().parse(context);
        }

        Social.get().getTextProcessor().send(this, parsedMessage, context.messageChannelType());
    }

    public void sendParsableMessage(@NonNull SocialParserContext context) {
        sendParsableMessage(context, false);
    }

    public void sendParsableMessage(@NonNull Component component, boolean playerInput) {
        SocialParserContext context = SocialParserContext.builder()
            .user(this)
            .message(component)
            .build();

        sendParsableMessage(context, playerInput);
    }

    public void sendParsableMessage(@NonNull Component component) {
        sendParsableMessage(component, false);
    }

    public void sendParsableMessage(@NonNull String message, boolean playerInput) {
        sendParsableMessage(Component.text(message), playerInput);
    }

    public void sendParsableMessage(@NonNull String message) {
        sendParsableMessage(message, false);
    }

}
