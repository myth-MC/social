package ovh.mythmc.social.api.users;

import lombok.*;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;

import java.util.ArrayList;
import java.util.Optional;
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

    public static Dummy dummy() { return new Dummy(null); }

    public static Dummy dummy(ChatChannel channel) { return new Dummy(channel); }

    public static final class Dummy extends SocialUser {

        private Dummy(ChatChannel channel) {
            super(UUID.nameUUIDFromBytes("#Dummy".getBytes()), channel, false, null, 0, "Dummy", null);
        }

    }

    @DatabaseField(id = true)
    private @NotNull UUID uuid;

    private ChatChannel mainChannel;

    private boolean socialSpy = false;

    @Getter(AccessLevel.PROTECTED)
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> blockedChannels = new ArrayList<>();

    private long latestMessageInMilliseconds;

    @DatabaseField
    @Getter(AccessLevel.PRIVATE)
    private String cachedNickname = null;

    private @Nullable SocialUserCompanion companion;

    public Optional<Player> player() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    @Deprecated(since = "0.4", forRemoval = true)
    public @Nullable Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean isCompanion() {
        return companion != null;
    }

    public @Nullable CommandSender asCommandSender() {
        if (getPlayer() != null)
            return getPlayer();

        return Bukkit.getConsoleSender();
    }

    public @Nullable GroupChatChannel getGroupChatChannel() {
        return Social.get().getChatManager().getGroupChannelByUser(this);
    }

    public Component displayName() {
        player().ifPresent(player -> cachedNickname = ChatColor.stripColor(player.getDisplayName()));
        return Component.text(cachedNickname);
    }

    public String getNickname() {
        if (player().isPresent())
            cachedNickname = ChatColor.stripColor(player().get().getDisplayName());

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

        Social.get().getTextProcessor().send(this, parsedMessage, context.messageChannelType(), context.channel());
    }

    public void sendParsableMessage(@NonNull SocialParserContext context) {
        sendParsableMessage(context, false);
    }

    public void sendParsableMessage(@NonNull Component component, boolean playerInput) {
        SocialParserContext context = SocialParserContext.builder(this, component).build();

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
