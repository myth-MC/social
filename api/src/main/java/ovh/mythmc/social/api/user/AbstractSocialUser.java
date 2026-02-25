package ovh.mythmc.social.api.user;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.TextComponent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.channel.SocialChannelPostSwitch;
import ovh.mythmc.social.api.callback.channel.SocialChannelPostSwitchCallback;
import ovh.mythmc.social.api.callback.channel.SocialChannelPreSwitch;
import ovh.mythmc.social.api.callback.channel.SocialChannelPreSwitchCallback;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;
import ovh.mythmc.social.api.reaction.Reaction;
import ovh.mythmc.social.api.util.Mutable;

public abstract class AbstractSocialUser implements SocialUser {

    protected final UUID uuid;
    protected final String username;
    protected final Class<? extends SocialUser> rendererClass;

    protected final Set<String> blockedChannelNames = ConcurrentHashMap.newKeySet();

    protected final Mutable<TextComponent> displayName = Mutable.empty();
    protected final Mutable<ChatChannel> mainChannel = Mutable.empty();
    protected final Mutable<Boolean> socialSpy = Mutable.of(false);
    protected final Mutable<Long> lastMessageTimestamp = Mutable.of(0L);
    protected final Mutable<UUID> lastPrivateMessageRecipient = Mutable.empty();

    protected volatile SocialUserCompanion companion;

    protected AbstractSocialUser(
            @NotNull UUID uuid,
            @NotNull String username,
            @NotNull Class<? extends SocialUser> rendererClass) {
        this.uuid = uuid;
        this.username = username;
        this.rendererClass = rendererClass;

        this.mainChannel.onChange((oldChannel, newChannel) -> {
            final var preSwitchCallback = new SocialChannelPreSwitch(this, oldChannel);
            SocialChannelPreSwitchCallback.INSTANCE.invoke(preSwitchCallback);

            if (preSwitchCallback.cancelled()) {
                mainChannel.set(oldChannel);
                return;
            }

            final var postSwitchCallback = new SocialChannelPostSwitch(this, oldChannel,
                    newChannel);
            SocialChannelPostSwitchCallback.INSTANCE.invoke(postSwitchCallback);
        });
    }

    @Override
    public @NotNull Mutable<ChatChannel> mainChannel() {
        return this.mainChannel;
    }

    @Override
    public @NotNull Optional<GroupChatChannel> groupChannel() {
        return Social.get().getChatManager().groupChannelByUser(this);
    }

    @Override
    public @NotNull UUID uuid() {
        return this.uuid;
    }

    @Override
    public @NotNull String username() {
        return this.username;
    }

    @Override
    public @NotNull Mutable<TextComponent> displayName() {
        return this.displayName;
    }

    @Override
    public @NotNull Optional<SocialUserCompanion> companion() {
        return Optional.ofNullable(this.companion);
    }

    @Override
    public boolean canClearFromCache() {
        return !isOnline();
    }

    @Override
    public void playReaction(@NotNull Reaction reaction) {
        Social.get().getReactionFactory().play(this, reaction);
    }

    @Override
    public @NotNull Mutable<Boolean> socialSpy() {
        return this.socialSpy;
    }

    @Override
    public @NotNull Mutable<Long> lastMessageTimestamp() {
        return this.lastMessageTimestamp;
    }

    @Override
    public @NotNull Mutable<UUID> lastPrivateMessageRecipient() {
        return this.lastPrivateMessageRecipient;
    }

    @Override
    public @NotNull Set<String> blockedChannels() {
        return this.blockedChannelNames;
    }

    @Override
    public Class<? extends SocialUser> rendererClass() {
        return this.rendererClass;
    }

    void setCompanion(@NotNull SocialUserCompanion companion) {
        this.companion = companion;
    }

}
