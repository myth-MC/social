package ovh.mythmc.social.api.configuration.section.messages;

import de.exlll.configlib.Configuration;
import org.jetbrains.annotations.NotNull;

/**
 * Messages relating to general information.
 */
@Configuration
public class InfoMessages {

    private String playerJoinedGroup = "$(group_icon) <gray>$(clickable_nickname) has joined this group.</gray>";

    private String playerLeftGroup = "$(group_icon) <gray>$(clickable_nickname) has left this group.</gray>";

    private String groupLeaderChange = "$(group_icon) <gray>$(clickable_nickname) is now the leader of this group.</gray>";

    private String groupDisbanded = "$(group_icon) <gray>This group has been disbanded.</gray>";

    private String userMuted = "$(info_prefix) <gray>You have been muted in channel <yellow>$(channel)</yellow>.</gray>";

    private String userUnmuted = "$(info_prefix) <gray>You have been unmuted in channel <yellow>$(channel)</yellow>.</gray>";

    private String userMutedGlobally = "$(info_prefix) <gray>You have been muted globally.</gray>";

    private String userUnmutedGlobally = "$(info_prefix) <gray>You have been unmuted globally.</gray>";

    private String userOpenedPrivateChannel = "$(info_prefix) <gray>You have opened a <green>private channel</green> with $(formatted_nickname).</gray>";

    public @NotNull String getPlayerJoinedGroup() {
        return playerJoinedGroup;
    }

    public @NotNull String getPlayerLeftGroup() {
        return playerLeftGroup;
    }

    public @NotNull String getGroupLeaderChange() {
        return groupLeaderChange;
    }

    public @NotNull String getGroupDisbanded() {
        return groupDisbanded;
    }

    public @NotNull String getUserMuted() {
        return userMuted;
    }

    public @NotNull String getUserUnmuted() {
        return userUnmuted;
    }

    public @NotNull String getUserMutedGlobally() {
        return userMutedGlobally;
    }

    public @NotNull String getUserUnmutedGlobally() {
        return userUnmutedGlobally;
    }

    public @NotNull String getUserOpenedPrivateChannel() {
        return userOpenedPrivateChannel;
    }

}

