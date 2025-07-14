package ovh.mythmc.social.api.user;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import com.j256.ormlite.table.DatabaseTable;

import lombok.AccessLevel;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.database.model.DatabaseUser;
import ovh.mythmc.social.api.network.channel.S2CNetworkChannelWrapper;
import ovh.mythmc.social.api.network.payload.NetworkPayloadWrapper;
import ovh.mythmc.social.api.reaction.Reaction;
import ovh.mythmc.social.api.util.Mutable;

@DatabaseTable(tableName = "users")
@Setter(AccessLevel.PROTECTED)
public abstract class AbstractSocialUser extends DatabaseUser implements SocialUser, ForwardingAudience.Single {

    public static Dummy dummy() { return new Dummy(null); }

    public static Dummy dummy(ChatChannel channel) { return new Dummy(channel); }

    public abstract <T extends NetworkPayloadWrapper.ServerToClient> void sendCustomPayload(final @NotNull S2CNetworkChannelWrapper<T> channel, final @NotNull T payload);

    public abstract void playReaction(@NotNull Reaction reaction);

    private final Mutable<Long> latestMessageInMilliseconds = Mutable.of(0L);

    private ChatChannel mainChannel;

    private final Mutable<Boolean> socialSpy = Mutable.of(false);

    private SocialUserCompanion companion;

    private UUID latestPrivateMessageRecipient;

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
    public @NotNull Mutable<Boolean> socialSpy() {
        return this.socialSpy;
    }

    @Override
    public @NotNull ArrayList<String> blockedChannels() {
        return this.blockedChannels;
    }

    @Override
    public @NotNull Mutable<Long> latestMessageInMilliseconds() {
        return this.latestMessageInMilliseconds;
    }

    @Override
    public boolean clearFromCache() {
        return !isOnline();
    }

    @Override
    public @NotNull Optional<SocialUserCompanion> companion() {
        return Optional.ofNullable(companion);
    }

    @Override
    public @NotNull Optional<GroupChatChannel> group() {
        return Social.registries().channels().valuesByType(GroupChatChannel.class).stream()
            .filter(group -> group.isMember(this.uuid))
            .findAny();
    }

    public Optional<AbstractSocialUser> latestPrivateMessageRecipient() {
        return Social.get().getUserService().getByUuid(latestPrivateMessageRecipient);
    }

    protected void setLatestPrivateMessageRecipient(UUID recipientUuid) {
        this.latestPrivateMessageRecipient = recipientUuid;
    }

    // Send social messages
    public void sendParsableMessage(@NotNull SocialParserContext context, boolean playerInput) {
        Component parsedMessage;

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
        public @NotNull Class<? extends SocialUser> rendererClass() {
            return Dummy.class;
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
        public @NotNull String name() {
            return "Dummy";
        }

        @Override
        public <T extends NetworkPayloadWrapper.ServerToClient> void sendCustomPayload(@NotNull S2CNetworkChannelWrapper<T> channel, @NotNull T payload) {
        }

        @Override
        public void playReaction(@NotNull Reaction reaction) {
        }

    }
    
}
