package ovh.mythmc.social.api.players;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SocialPlayerManager {

    public static final SocialPlayerManager instance = new SocialPlayerManager();
    private static final List<SocialPlayer> playerList = new ArrayList<>();

    public @NotNull List<SocialPlayer> get() {
        return List.copyOf(playerList);
    }

    public SocialPlayer get(final @NotNull UUID uuid) {
        for (SocialPlayer player : playerList) {
            if (player.getUuid().equals(uuid))
                return player;
        }

        return null;
    }

    public void registerSocialPlayer(final @NotNull SocialPlayer socialPlayer) {
        playerList.add(socialPlayer);
    }

    public void registerSocialPlayer(final @NotNull UUID uuid) {
        // Todo: recover data from last session
        String defaultChatChannelName = Social.get().getConfig().getSettings().getChat().getDefaultChannel();
        ChatChannel defaultChatChannel = Social.get().getChatManager().getChannel(defaultChatChannelName);

        SocialPlayer socialPlayer = new SocialPlayer(uuid);
        socialPlayer.setMuted(false);
        socialPlayer.setSocialSpy(false);

        if (defaultChatChannel == null) {
            Social.get().getLogger().warn("Default channel '" + defaultChatChannelName + "' is unavailable!");
        } else {
            if (!defaultChatChannel.getMembers().contains(uuid))
                defaultChatChannel.addMember(uuid);

            socialPlayer.setMainChannel(defaultChatChannel);
        }

        registerSocialPlayer(socialPlayer);
    }

    public void unregisterSocialPlayer(final @NotNull SocialPlayer socialPlayer) {
        playerList.remove(socialPlayer);
    }

}
