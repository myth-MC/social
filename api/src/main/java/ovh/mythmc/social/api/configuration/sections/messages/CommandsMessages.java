package ovh.mythmc.social.api.configuration.sections.messages;

import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class CommandsMessages {

    private String channelChanged = "<gray>You are now chatting in channel @channel.</gray>";

    private String nicknameChanged = "<gray>Your nickname is now <blue>@nickname</blue>.</gray>";

    private String nicknameChangedOthers = "<gray><blue>%s</blue>'s nickname has been set to <green>%s</green></gray>";

    private String nicknameResetted = "<gray>Your nickname has been resetted.</gray>";

    private String nicknameResettedOthers = "<gray><blue>%s</blue>'s name has been resetted.</gray>";

    private String pluginReloaded = "<green>Plugin has been reloaded.</green>";

    private String socialSpyStatusChanged = "<gray>Social spy status has been set to @socialspy.</gray>";

}
