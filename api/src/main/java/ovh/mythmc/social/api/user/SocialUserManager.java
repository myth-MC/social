package ovh.mythmc.social.api.user;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.user.SocialUserMuteStatusChange;
import ovh.mythmc.social.api.callback.user.SocialUserMuteStatusChangeCallback;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.channel.PrivateChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialUserManager {

    public static final SocialUserManager instance = new SocialUserManager();

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
