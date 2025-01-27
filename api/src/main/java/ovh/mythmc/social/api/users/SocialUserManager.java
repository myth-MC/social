package ovh.mythmc.social.api.users;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.database.SocialDatabase;
import ovh.mythmc.social.api.database.model.BlockedChannel;
import ovh.mythmc.social.api.database.model.IgnoredUser;
import ovh.mythmc.social.api.database.model.IgnoredUser.IgnoreScope;
import ovh.mythmc.social.api.events.chat.SocialChannelPostSwitchEvent;
import ovh.mythmc.social.api.events.chat.SocialChannelPreSwitchEvent;
import ovh.mythmc.social.api.events.users.SocialUserIgnoreEvent;
import ovh.mythmc.social.api.events.users.SocialUserMuteStatusChangeEvent;
import ovh.mythmc.social.api.events.users.SocialUserUnignoreEvent;

import java.util.Collection;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SocialUserManager {

    public static final SocialUserManager instance = new SocialUserManager();

    // Gets ONLINE users
    public @NotNull Collection<SocialUser> get() {
        return Bukkit.getOnlinePlayers().stream()
            .map(player -> get(player.getUniqueId()))
            .filter(user -> user != null)
            .toList();
    }

    public SocialUser get(final @NotNull UUID uuid) {
        SocialUser user = SocialDatabase.get().getUserByUuid(uuid);
        if (user != null && user.getMainChannel() == null)
            user.setMainChannel(Social.get().getChatManager().getDefaultChannel());
        
        return user;
    }

    public void register(final @NotNull SocialUser user) {
        SocialDatabase.get().create(user);
    }

    public void register(final @NotNull UUID uuid) {
        // Todo: recover data from last session
        String defaultChatChannelName = Social.get().getConfig().getSettings().getChat().getDefaultChannel();
        ChatChannel defaultChatChannel = Social.get().getChatManager().getDefaultChannel();

        SocialUser user = new SocialUser(uuid);
        user.setSocialSpy(false);

        if (defaultChatChannel == null) {
            Social.get().getLogger().warn("Default channel '" + defaultChatChannelName + "' is unavailable!");
        } else {
            if (!defaultChatChannel.getMembers().contains(uuid))
                defaultChatChannel.addMember(uuid);

            user.setMainChannel(defaultChatChannel);
        }

        register(user);
    }

    public void setMainChannel(final @NotNull SocialUser user,
                               final @NotNull ChatChannel chatChannel) {

        ChatChannel previousChannel = user.getMainChannel();

        SocialChannelPreSwitchEvent socialChannelPreSwitchEvent = new SocialChannelPreSwitchEvent(user, previousChannel, chatChannel);
        Bukkit.getPluginManager().callEvent(socialChannelPreSwitchEvent);

        if (socialChannelPreSwitchEvent.isCancelled())
            return;

        user.setMainChannel(chatChannel);
        SocialChannelPostSwitchEvent socialChannelPostSwitchEvent = new SocialChannelPostSwitchEvent(user, previousChannel, chatChannel);
        Bukkit.getPluginManager().callEvent(socialChannelPostSwitchEvent);

        SocialDatabase.get().update(user);
    }

    public void setLatestMessage(final @NotNull SocialUser user,
                                 final long latestMessageInMilliseconds) {

        user.setLatestMessageInMilliseconds(latestMessageInMilliseconds);

        SocialDatabase.get().update(user);
    }

    public void setSocialSpy(final @NotNull SocialUser user,
                             final boolean socialSpy) {

        user.setSocialSpy(socialSpy);

        SocialDatabase.get().update(user);
    }

    public boolean isGloballyMuted(final @NotNull SocialUser user) {
        return user.getBlockedChannels().containsAll(Social.get().getChatManager().getChannels().stream().map(channel -> channel.getName()).toList());
    }

    public boolean isMuted(final @NotNull SocialUser user, final @NotNull ChatChannel channel) {
        return user.getBlockedChannels().contains(channel.getName());
    }

    public void mute(final @NotNull SocialUser user, final @NotNull ChatChannel channel) {
        SocialUserMuteStatusChangeEvent event = new SocialUserMuteStatusChangeEvent(user, channel, true);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        BlockedChannel blockedChannel = new BlockedChannel(user, channel.getName());
        user.getBlockedChannels().add(blockedChannel);

        SocialDatabase.get().update(user);
    }

    public void unmute(final @NotNull SocialUser user, final @NotNull ChatChannel channel) {
        SocialUserMuteStatusChangeEvent event = new SocialUserMuteStatusChangeEvent(user, channel, false);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        BlockedChannel blockedChannel = SocialDatabase.get().getUserByUuid(user.getUuid()).getBlockedChannels().stream()
            .filter(u -> u.getChannelName().equals(channel.getName()))
            .findFirst().orElse(null);

        if (blockedChannel != null)
            user.getBlockedChannels().remove(blockedChannel);

        SocialDatabase.get().update(user);
    }

    public boolean isIgnored(final @NotNull SocialUser user, final @NotNull SocialUser target) {
        return user.getIgnoredUsers().stream()
            .map(ignoredUser -> ignoredUser.getTarget())
            .toList().contains(target.getUuid());
    }

    public IgnoreScope getIgnoreScope(final @NotNull SocialUser user, final @NotNull SocialUser target) {
        return user.getIgnoredUsers().stream()
            .filter(ignoredUser -> ignoredUser.getTarget().equals(target.getUuid()))
            .map(IgnoredUser::getScope)
            .findFirst().orElse(null);
    } 

    public void ignore(final @NotNull SocialUser user, final @NotNull SocialUser target, final @NotNull IgnoreScope scope) {
        SocialUserIgnoreEvent socialUserIgnoreEvent = new SocialUserIgnoreEvent(user, target, scope);
        Bukkit.getPluginManager().callEvent(socialUserIgnoreEvent);

        if (socialUserIgnoreEvent.isCancelled())
            return;

        SocialDatabase.get().create(new IgnoredUser(user, target.getUuid(), scope));
    }

    public void unignore(final @NotNull SocialUser user, final @NotNull SocialUser target) {
        SocialUserUnignoreEvent socialUserUnignoreEvent = new SocialUserUnignoreEvent(user, target);
        Bukkit.getPluginManager().callEvent(socialUserUnignoreEvent);

        if (socialUserUnignoreEvent.isCancelled())
            return;

        IgnoredUser ignoredUser = SocialDatabase.get().getUserByUuid(user.getUuid()).getIgnoredUsers().stream()
            .filter(u -> u.getTarget().equals(target.getUuid()))
            .findFirst().orElse(null);
        
        if (ignoredUser == null)
            return;

        SocialDatabase.get().delete(ignoredUser);
    }

}
