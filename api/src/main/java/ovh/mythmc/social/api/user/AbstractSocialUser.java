package ovh.mythmc.social.api.user;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import com.j256.ormlite.table.DatabaseTable;

import lombok.AccessLevel;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.reaction.Reaction;

@DatabaseTable(tableName = "users")
@Setter(AccessLevel.PROTECTED)
public abstract class AbstractSocialUser<P extends Object> implements SocialUser<P>, SocialUserAudienceWrapper {

    public final static Dummy<?> dummy() { return new Dummy<>(null); }

    public final static Dummy<?> dummy(ChatChannel channel) { return new Dummy<>(channel); }

    protected final P player;

    protected abstract void sendCustomPayload(String channel, byte[] payload);

    public abstract void playReaction(@NotNull Reaction reaction);

    private @NotNull UUID uuid;

    private ArrayList<String> blockedChannels = new ArrayList<>();

    private String cachedName;

    private Style displayNameStyle;

    private long latestMessageInMilliseconds = 0L;

    private ChatChannel mainChannel;

    private boolean socialSpy = false;

    private SocialUserCompanion companion;

    protected AbstractSocialUser(final UUID uuid, final P player, final String name) {
        this.uuid = uuid;
        this.player = player;
        this.cachedName = name;
    }

    protected AbstractSocialUser(final UUID uuid, final P player, final String name, final ChatChannel channel) {
        this.uuid = uuid;
        this.player = player;
        this.cachedName = name;
        this.mainChannel = channel;
    }

    @Override
    public SocialUser<?> user() {
        return this;
    }

    @Override
    public Optional<P> player() {
        return Optional.ofNullable(player);
    }

    @Override
    public UUID uuid() {
        return uuid;
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
    public ArrayList<String> blockedChannels() {
        return blockedChannels;
    }

    @Override
    public long latestMessageInMilliseconds() {
        return latestMessageInMilliseconds;
    }

    @Override
    public Style displayNameStyle() {
        return displayNameStyle;
    }

    @Override
    public Optional<SocialUserCompanion> companion() {
        return Optional.ofNullable(companion);
    }

    @Override
    public Optional<GroupChatChannel> group() {
        return Optional.ofNullable(Social.get().getChatManager().getGroupChannelByUser(this));
    }

    @Override
    public String cachedName() {
        return cachedName;
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

    public static final class Dummy<P> extends AbstractSocialUser<P> {

        private Dummy(ChatChannel channel) {
            super(UUID.nameUUIDFromBytes("#Dummy".getBytes()), null, "Dummy", channel);
        }

        @Override
        public Optional<P> player() {
            return Optional.ofNullable(null);
        }

        @Override
        public Audience audience() {
            return null;
        }

        @Override
        public void name(@NotNull String name) {
        }

        @Override
        public boolean checkPermission(@NotNull String permission) {
            return false;
        }

        @Override
        public String name() {
            return "Dummy";
        }

        @Override
        protected void sendCustomPayload(String channel, byte[] payload) {
        }

        @Override
        public void playReaction(@NotNull Reaction reaction) {
        }

    }
    
}
