package ovh.mythmc.social.api.configuration.sections.messages;

import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class CommandsMessages {

    private String channelChanged = "$info_prefix <gray>You are now chatting in channel $channel.</gray>";

    private String nicknameChanged = "$info_prefix <gray>Your nickname is now <blue>$nickname</blue>.</gray>";

    private String nicknameChangedOthers = "$success_prefix <gray><blue>%s</blue>'s nickname has been set to <green>%s</green></gray>";

    private String nicknameReset = "$info_prefix <gray>Your nickname has been reset.</gray>";

    private String nicknameResetOthers = "$success_prefix <gray><blue>%s</blue>'s name has been reset.</gray>";

    private String pluginReloaded = "$success_prefix <green>Plugin has been reloaded.</green>";

    private String pluginReloadedModulesWarning = "$warning_prefix <yellow>Modules cannot be enabled/disabled on the fly.</yellow>";

    private String socialSpyStatusChanged = "$info_prefix <gray>Social spy status has been set to $socialspy.</gray>";

    private String createdGroup = "$success_prefix <green>You've created a new group. You can see the invite code by running <white><click:run_command:'/social:group code'><hover:show_text:'$group_code'>/group code</hover></click></white>. Other players can join the group by using <white>/group join <code></white>.</green>";

    private String groupCode = "$info_prefix <gray>Your group's code is <blue>$group_code</blue>.</gray>";

    private String joinedGroup = "$success_prefix <green>You've joined group <blue>$group</blue>.</green>";

    private String leftGroup = "$success_prefix <green>You've left group <blue>$group</blue>.</green>";

    private String groupDisbanded = "$success_prefix <green>Your group has been disbanded.</green>";

    private String groupAliasChanged = "$info_prefix <gray>This group's alias has been set to <blue>$group</blue></gray>";

}
