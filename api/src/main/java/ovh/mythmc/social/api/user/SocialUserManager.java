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
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.database.SocialDatabase;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialUserManager {

    public static final SocialUserManager instance = new SocialUserManager();

    private static DatabaseUser getDatabaseUser(@NotNull AbstractSocialUser<? extends Object> user) {
        return DatabaseUser.fromUser(user);
    }

    public void setMainChannel(@NotNull AbstractSocialUser<? extends Object> user, @NotNull ChatChannel channel) {
        ChatChannel previousChannel = user.mainChannel();

        var preSwitchCallback = new SocialChannelPreSwitch(user, channel);
        SocialChannelPreSwitchCallback.INSTANCE.invoke(preSwitchCallback);

        if (preSwitchCallback.cancelled())
            return;

        user.setMainChannel(channel);
        
        var postSwitchCallback = new SocialChannelPostSwitch(user, previousChannel, channel);
        SocialChannelPostSwitchCallback.INSTANCE.invoke(postSwitchCallback);

        SocialDatabase.get().update(getDatabaseUser(user));
    }

    public void setLatestMessage(@NotNull AbstractSocialUser<? extends Object> user, long latestMessageInMilliseconds) {
        user.setLatestMessageInMilliseconds(latestMessageInMilliseconds);

        SocialDatabase.get().update(getDatabaseUser(user));
    }

    public void setSocialSpy(@NotNull AbstractSocialUser<? extends Object> user, boolean socialSpy) {
        user.setSocialSpy(socialSpy);

        SocialDatabase.get().update(getDatabaseUser(user));
    }

    public void setDisplayName(@NotNull AbstractSocialUser<? extends Object> user, @NotNull String displayName) {
        user.setCachedName(displayName);
        user.name(displayName);

        SocialDatabase.get().update(getDatabaseUser(user));
    }

    public void setDisplayNameStyle(@NotNull AbstractSocialUser<? extends Object> user, @NotNull Style style) {
        user.setDisplayNameStyle(style);

        SocialDatabase.get().update(getDatabaseUser(user));
    }

    public boolean isGloballyMuted(final @NotNull AbstractSocialUser<? extends Object> user) {
        return user.blockedChannels().containsAll(Social.get().getChatManager().getChannels().stream().map(channel -> channel.getName()).toList());
    }

    public boolean isMuted(final @NotNull AbstractSocialUser<? extends Object> user, final @NotNull ChatChannel channel) {
        return user.blockedChannels().contains(channel.getName());
    }

    public void mute(final @NotNull AbstractSocialUser<? extends Object> user, final @NotNull ChatChannel channel) {
        var callback = new SocialUserMuteStatusChange(user, channel, true);
        SocialUserMuteStatusChangeCallback.INSTANCE.invoke(callback);

        if (callback.cancelled())
            return;

        user.blockedChannels().add(channel.getName());
        SocialDatabase.get().update(getDatabaseUser(user));
    }

    public void unmute(final @NotNull AbstractSocialUser<? extends Object> user, final @NotNull ChatChannel channel) {
        var callback = new SocialUserMuteStatusChange(user, channel, false);
        SocialUserMuteStatusChangeCallback.INSTANCE.invoke(callback);

        if (callback.cancelled())
            return;

        user.blockedChannels().remove(channel.getName());
        SocialDatabase.get().update(getDatabaseUser(user));
    }

    public void enableCompanion(final @NotNull AbstractSocialUser<? extends Object> user) {
        user.setCompanion(new SocialUserCompanion(user));

        SocialDatabase.get().update(getDatabaseUser(user));
    }

    public void disableCompanion(final @NotNull AbstractSocialUser<? extends Object> user) {
        user.setCompanion(null);

        SocialDatabase.get().update(getDatabaseUser(user));
    }
    
}
