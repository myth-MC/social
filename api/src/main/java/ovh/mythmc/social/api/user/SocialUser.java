package ovh.mythmc.social.api.user;

import lombok.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Experimental;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.database.persister.AdventureStylePersister;

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

    public final static Dummy dummy() { return new Dummy(null); }

    public final static Dummy dummy(ChatChannel channel) { return new Dummy(channel); }

    @DatabaseField(id = true)
    private @NotNull UUID uuid;

    private ChatChannel mainChannel;

    private boolean socialSpy = false;

    @Getter(AccessLevel.PROTECTED)
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> blockedChannels = new ArrayList<>();

    private long latestMessageInMilliseconds;

    @DatabaseField(columnName = "cachedNickname")
    private String cachedDisplayName;

    @DatabaseField(persisterClass = AdventureStylePersister.class)
    private Style displayNameStyle;

    @Getter(AccessLevel.PRIVATE)
    private SocialUserCompanion companion;

    public Optional<Player> player() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    @Deprecated(since = "0.4", forRemoval = true)
    public @Nullable Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @Deprecated(forRemoval = true)
    public boolean isCompanion() {
        return companion != null;
    }

    @Experimental
    public Optional<SocialUserCompanion> companion() {
        return Optional.ofNullable(companion);
    }

    public @Nullable CommandSender asCommandSender() {
        if (getPlayer() != null)
            return getPlayer();

        return Bukkit.getConsoleSender();
    }

    @Deprecated(forRemoval = true)
    public boolean hasGroupChatChannel() {
        return getGroupChatChannel() != null;
    }

    @Deprecated(forRemoval = true)
    public @Nullable GroupChatChannel getGroupChatChannel() {
        return Social.get().getChatManager().getGroupChannelByUser(this);
    }

    public Optional<GroupChatChannel> group() {
        return Optional.ofNullable(Social.get().getChatManager().getGroupChannelByUser(this));
    }

    public Component displayName() {
        var displayName = Component.text(cachedDisplayName);

        if (displayNameStyle != null)
            return displayName.style(displayNameStyle);

        return displayName;
    }

    @Deprecated(forRemoval = true)
    public String getNickname() {
        return cachedDisplayName;
    }

    // Send social messages
    public void sendParsableMessage(@NonNull SocialParserContext context, boolean playerInput) {
        if (player().isEmpty())
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

    public static final class Dummy extends SocialUser {

        private Dummy(ChatChannel channel) {
            super(UUID.nameUUIDFromBytes("#Dummy".getBytes()), channel, false, null, 0, "Dummy", null, null);
        }

    }

}
