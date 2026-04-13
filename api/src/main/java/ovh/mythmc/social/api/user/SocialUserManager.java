package ovh.mythmc.social.api.user;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.user.SocialUserMuteStatusChange;
import ovh.mythmc.social.api.callback.user.SocialUserMuteStatusChangeCallback;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.channel.PrivateChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;

/**
 * This class provides various user-related utilities.
 * 
 * <p>
 * Most methods in this class are used internally and shouldn't be used by
 * external plugins unless necessary.
 * </p>
 */
public final class SocialUserManager {

    public static final SocialUserManager instance = new SocialUserManager();

    private SocialUserManager() {}

    /**
     * Announces a {@link ChatChannel} switch to the {@link SocialUser} and
     * switches their main channel afterwards.
     * @param user    the {@link SocialUser} to send the message to
     * @param channel the {@link ChatChannel} to switch to
     */
    public void announceChannelSwitch(@NotNull SocialUser user, @NotNull ChatChannel channel) {
        user.mainChannel().set(channel);
        
        if (user.companion().isPresent()) // Don't send message to users who use the companion mod
            return;

        if (channel instanceof PrivateChatChannel privateChatChannel) {
            final var context = SocialParserContext
                    .builder(privateChatChannel.getRecipientForSender(user),
                            Component.text(Social.get().getConfig().getMessages().getCommands()
                                    .getChannelChangedToPrivateMessage()))
                    .channel(channel)
                    .build();

            user.sendParsableMessage(context);
            return;
        }

        final var context = SocialParserContext
                .builder(user,
                        Component.text(
                                Social.get().getConfig().getMessages().getCommands().getChannelChanged()))
                .channel(channel)
                .build();

        Social.get().getTextProcessor().parseAndSend(context);
    }

    /**
     * Checks if a {@link SocialUser} is muted across all channels.
     * @param user the {@link SocialUser} to check
     * @return     {@code true} if the {@link SocialUser} is muted globally,
     *             {@code false} otherwise
     */
    public boolean isGloballyMuted(final @NotNull SocialUser user) {
        return user.blockedChannels()
                .containsAll(Social.registries().channels().values().stream().map(ChatChannel::name).toList());
    }

    /**
     * Checks if a {@link SocialUser} is muted in a specific {@link ChatChannel}.
     * @param user    the {@link SocialUser} to check
     * @param channel the {@link ChatChannel} to check
     * @return        {@code true} if the {@link SocialUser} is muted in {@link ChatChannel},
     *                {@code false} otherwise
     */
    public boolean isMuted(final @NotNull SocialUser user, final @NotNull ChatChannel channel) {
        return user.blockedChannels().contains(channel.name());
    }

    /**
     * Mutes a {@link SocialUser} in a specific {@link ChatChannel}.
     * @param user    the {@link SocialUser} to mute
     * @param channel the {@link ChatChannel} where the user will be muted
     */
    public void mute(final @NotNull SocialUser user, final @NotNull ChatChannel channel) {
        var callback = new SocialUserMuteStatusChange(user, channel, true);
        SocialUserMuteStatusChangeCallback.INSTANCE.invoke(callback);

        if (callback.cancelled())
            return;

        user.blockedChannels().add(channel.name());
    }

    /**
     * Unmutes a {@link SocialUser} in a specific {@link ChatChannel}.
     * @param user    the {@link SocialUser} to unmuet
     * @param channel the {@link ChatChannel} where the user will be unmuted
     */
    public void unmute(final @NotNull SocialUser user, final @NotNull ChatChannel channel) {
        var callback = new SocialUserMuteStatusChange(user, channel, false);
        SocialUserMuteStatusChangeCallback.INSTANCE.invoke(callback);

        if (callback.cancelled())
            return;

        user.blockedChannels().remove(channel.name());
    }

    /**
     * Enables the companion mod features for a specific {@link SocialUser}.
     * @param user the {@link SocialUser} to enable the companion features for
     */
    public void enableCompanion(final @NotNull SocialUser user) {
        ((AbstractSocialUser) user).setCompanion(new SocialUserCompanion(user));
    }

    /**
     * Disables the companion mod features for a specific {@link SocialUser}.
     * @param user the {@link SocialUser} to disable the companion features for
     */
    public void disableCompanion(final @NotNull SocialUser user) {
        ((AbstractSocialUser) user).setCompanion(null);
    }

}

