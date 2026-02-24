package ovh.mythmc.social.api.user;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.user.SocialUserMuteStatusChange;
import ovh.mythmc.social.api.callback.user.SocialUserMuteStatusChangeCallback;
import ovh.mythmc.social.api.chat.channel.ChatChannel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialUserManager {

    public static final SocialUserManager instance = new SocialUserManager();

    @Deprecated(forRemoval = true)
    public void setMainChannel(@NotNull SocialUser user, @NotNull ChatChannel channel, boolean informUser) {
        user.mainChannel().set(channel);
    }

    @Deprecated(forRemoval = true)
    public void setLatestPrivateMessageRecipient(@NotNull SocialUser user,
            @NotNull SocialUser recipient) {
        user.lastPrivateMessageRecipient().set(recipient.uuid());
    }

    public boolean isGloballyMuted(final @NotNull SocialUser user) {
        return user.blockedChannels()
                .containsAll(Social.registries().channels().values().stream().map(ChatChannel::name).toList());
    }

    public boolean isMuted(final @NotNull SocialUser user, final @NotNull ChatChannel channel) {
        return user.blockedChannels().contains(channel.name());
    }

    public void mute(final @NotNull SocialUser user, final @NotNull ChatChannel channel) {
        var callback = new SocialUserMuteStatusChange(user, channel, true);
        SocialUserMuteStatusChangeCallback.INSTANCE.invoke(callback);

        if (callback.cancelled())
            return;

        user.blockedChannels().add(channel.name());
    }

    public void unmute(final @NotNull SocialUser user, final @NotNull ChatChannel channel) {
        var callback = new SocialUserMuteStatusChange(user, channel, false);
        SocialUserMuteStatusChangeCallback.INSTANCE.invoke(callback);

        if (callback.cancelled())
            return;

        user.blockedChannels().remove(channel.name());
    }

    public void enableCompanion(final @NotNull SocialUser user) {
        ((AbstractSocialUser) user).setCompanion(new SocialUserCompanion(user));
    }

    public void disableCompanion(final @NotNull SocialUser user) {
        ((AbstractSocialUser) user).setCompanion(null);
    }

}
