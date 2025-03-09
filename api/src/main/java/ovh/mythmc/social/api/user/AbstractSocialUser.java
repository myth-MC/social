package ovh.mythmc.social.api.user;

import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import com.j256.ormlite.table.DatabaseTable;

import lombok.AccessLevel;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.database.model.DatabaseUser;
import ovh.mythmc.social.api.reaction.Reaction;

@DatabaseTable(tableName = "users")
@Setter(AccessLevel.PROTECTED)
public abstract class AbstractSocialUser extends DatabaseUser implements SocialUser, ForwardingAudience.Single {

    public static Dummy dummy() { return new Dummy(null); }

    public static Dummy dummy(ChatChannel channel) { return new Dummy(channel); }

    protected abstract void sendCustomPayload(String channel, byte[] payload);

    public abstract void playReaction(@NotNull Reaction reaction);

    private long latestMessageInMilliseconds = 0L;

    private ChatChannel mainChannel;

    private boolean socialSpy = false;

    private SocialUserCompanion companion;

    protected AbstractSocialUser() {
    }

    protected AbstractSocialUser(final UUID uuid) {
        super(uuid);
    }

    protected AbstractSocialUser(final UUID uuid, final ChatChannel channel) {
        super(uuid);
        this.mainChannel = channel;
    }

    @Override
    public ChatChannel mainChannel() {
        return mainChannel;
    }

    @Override
    public boolean socialSpy() {
        return socialSpy;
    }

    @Override
    public long latestMessageInMilliseconds() {
        return latestMessageInMilliseconds;
    }

    @Override
    public boolean clearFromCache() {
        return !isOnline();
    }

    @Override
    public Optional<SocialUserCompanion> companion() {
        return Optional.ofNullable(companion);
    }

    @Override
    public Optional<GroupChatChannel> group() {
        return Optional.ofNullable(Social.get().getChatManager().getGroupChannelByUser(this));
    }

    protected void setCachedDisplayName(String cachedDisplayName) {
        this.cachedDisplayName = cachedDisplayName;
    }

    protected void setDisplayNameStyle(Style style) {
        this.displayNameStyle = style;
    }

    // Send social messages
    public void sendParsableMessage(@NotNull SocialParserContext context, boolean playerInput) {
        Component parsedMessage = null;

        if (playerInput) {
            parsedMessage = Social.get().getTextProcessor().parsePlayerInput(context);
        } else {
            parsedMessage = Social.get().getTextProcessor().parse(context);
        }

        Social.get().getTextProcessor().send(this, parsedMessage, context.messageChannelType(), context.channel());
    }

    public void sendParsableMessage(@NotNull SocialParserContext context) {
        sendParsableMessage(context, false);
    }

    public void sendParsableMessage(@NotNull Component component, boolean playerInput) {
        SocialParserContext context = SocialParserContext.builder(this, component).build();

        sendParsableMessage(context, playerInput);
    }

    public void sendParsableMessage(@NotNull Component component) {
        sendParsableMessage(component, false);
    }

    public void sendParsableMessage(@NotNull String message, boolean playerInput) {
        sendParsableMessage(Component.text(message), playerInput);
    }

    public void sendParsableMessage(@NotNull String message) {
        sendParsableMessage(message, false);
    }

    public static final class Dummy extends AbstractSocialUser {

        private Dummy(ChatChannel channel) {
            super(UUID.nameUUIDFromBytes("#Dummy".getBytes()), channel);
        }

        @Override
        public @NotNull Audience audience() {
            return SocialAdventureProvider.get().console();
        }

        @Override
        public void name(@NotNull String name) {
        }

        @Override
        public boolean checkPermission(@NotNull String permission) {
            return false;
        }

        @Override
        public boolean isOnline() {
            return false;
        }

        @Override
        public String name() {
            return "Dummy";
        }

        @Override
        public String cachedDisplayName() {
            return name();
        }

        @Override
        protected void sendCustomPayload(String channel, byte[] payload) {
        }

        @Override
        public void playReaction(@NotNull Reaction reaction) {
        }

    }
    
}
