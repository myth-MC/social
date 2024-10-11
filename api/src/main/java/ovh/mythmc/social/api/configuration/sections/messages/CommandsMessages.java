package ovh.mythmc.social.api.configuration.sections.messages;

import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class CommandsMessages {

    private String channelChanged = "$info_prefix <gray>You are now chatting in channel $channel.</gray>";

    private String nicknameChanged = "$info_prefix <gray>Your nickname is now <blue>$nickname</blue>.</gray>";

    private String nicknameChangedOthers = "$success_prefix <gray><blue>%s</blue>'s nickname has been set to <green>%s</green></gray>";

    private String nicknameResetted = "$info_prefix <gray>Your nickname has been resetted.</gray>";

    private String nicknameResettedOthers = "$success_prefix <gray><blue>%s</blue>'s name has been resetted.</gray>";

    private String pluginReloaded = "$success_prefix <green>Plugin has been reloaded.</green>";

    private String pluginReloadedModulesWarning = "$warning_prefix <yellow>Modules cannot be enabled/disabled on the fly.</yellow>";

    private String socialSpyStatusChanged = "$info_prefix <gray>Social spy status has been set to $socialspy.</gray>";

    private String createdGroup = "$success_prefix <green>You've created a new group. You can see the invite code by running <white><click:run_command:'/social:group code'>/group code</click></white> or send it to a player by using <white>/group invite <player_name></white>.</green>";

    private String groupCode = "$info_prefix <gray>Your group's code is <blue>%s</blue>.</gray>";

    private String joinedGroup = "$success_prefix <green>You've joined group <blue>%s</blue>.</green>";

    private String leftGroup = "$success_prefix <green>You've left group <blue>%s</blue>.</green>";

    private String groupDisbanded = "$success_prefix <green>Your group has been disbanded.</green>";

}
