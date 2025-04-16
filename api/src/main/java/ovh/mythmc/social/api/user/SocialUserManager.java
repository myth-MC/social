package ovh.mythmc.social.api.user;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.format.Style;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.channel.SocialChannelPostSwitch;
import ovh.mythmc.social.api.callback.channel.SocialChannelPostSwitchCallback;
import ovh.mythmc.social.api.callback.channel.SocialChannelPreSwitch;
import ovh.mythmc.social.api.callback.channel.SocialChannelPreSwitchCallback;
import ovh.mythmc.social.api.callback.user.SocialUserMuteStatusChange;
import ovh.mythmc.social.api.callback.user.SocialUserMuteStatusChangeCallback;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.database.SocialDatabase;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialUserManager {

    public static final SocialUserManager instance = new SocialUserManager();

    public void setMainChannel(@NotNull AbstractSocialUser user, @NotNull ChatChannel channel, boolean informUser) {
        ChatChannel previousChannel = user.mainChannel();

        var preSwitchCallback = new SocialChannelPreSwitch(user, channel, informUser);
        SocialChannelPreSwitchCallback.INSTANCE.invoke(preSwitchCallback);

        if (preSwitchCallback.cancelled())
            return;

        user.setMainChannel(channel);
        
        var postSwitchCallback = new SocialChannelPostSwitch(user, preSwitchCallback.informUser(), previousChannel, channel);
        SocialChannelPostSwitchCallback.INSTANCE.invoke(postSwitchCallback);

        SocialDatabase.get().update(user);
    }

    public void setLatestMessage(@NotNull AbstractSocialUser user, long latestMessageInMilliseconds) {
        user.setLatestMessageInMilliseconds(latestMessageInMilliseconds);

        SocialDatabase.get().update(user);
    }

    public void setSocialSpy(@NotNull AbstractSocialUser user, boolean socialSpy) {
        user.setSocialSpy(socialSpy);

        SocialDatabase.get().update(user);
    }

    public void setDisplayName(@NotNull AbstractSocialUser user, @NotNull String displayName) {
        user.setCachedDisplayName(displayName);
        user.name(displayName);

        SocialDatabase.get().update(user);
    }

    public void setDisplayNameStyle(@NotNull AbstractSocialUser user, @NotNull Style style) {
        user.setDisplayNameStyle(style);

        SocialDatabase.get().update(user);
    }

    public void setLatestPrivateMessageRecipient(@NotNull AbstractSocialUser user, @NotNull AbstractSocialUser recipient) {
        user.setLatestPrivateMessageRecipient(recipient.uuid());
    }

    public boolean isGloballyMuted(final @NotNull AbstractSocialUser user) {
        return user.blockedChannels().containsAll(Social.registries().channels().values().stream().map(ChatChannel::name).toList());
    }

    public boolean isMuted(final @NotNull AbstractSocialUser user, final @NotNull ChatChannel channel) {
        return user.blockedChannels().contains(channel.name());
    }

    public void mute(final @NotNull AbstractSocialUser user, final @NotNull ChatChannel channel) {
        var callback = new SocialUserMuteStatusChange(user, channel, true);
        SocialUserMuteStatusChangeCallback.INSTANCE.invoke(callback);

        if (callback.cancelled())
            return;

        user.blockedChannels().add(channel.name());
        SocialDatabase.get().update(user);
    }

    public void unmute(final @NotNull AbstractSocialUser user, final @NotNull ChatChannel channel) {
        var callback = new SocialUserMuteStatusChange(user, channel, false);
        SocialUserMuteStatusChangeCallback.INSTANCE.invoke(callback);

        if (callback.cancelled())
            return;

        user.blockedChannels().remove(channel.name());
        SocialDatabase.get().update(user);
    }

    public void enableCompanion(final @NotNull AbstractSocialUser user) {
        user.setCompanion(new SocialUserCompanion(user));

        SocialDatabase.get().update(user);
    }

    public void disableCompanion(final @NotNull AbstractSocialUser user) {
        user.setCompanion(null);

        SocialDatabase.get().update(user);
    }
    
}
