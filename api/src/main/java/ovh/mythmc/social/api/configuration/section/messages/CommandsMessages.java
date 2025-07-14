package ovh.mythmc.social.api.configuration.section.messages;

import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class CommandsMessages {

    private String channelChanged = "$(info_prefix) <gray>You are now chatting in channel $(channel).</gray>";

    private String channelChangedToPrivateMessage = "$(info_prefix) <gray>You are now chatting with <blue>$(nickname)</blue>.</gray>";

    private String nicknameChanged = "$(info_prefix) <gray>Your nickname is now <blue>$(nickname)</blue>.</gray>";

    private String nicknameChangedOthers = "$(success_prefix) <gray><blue>%s</blue>'s nickname has been set to <green>%s</green></gray>";

    private String nicknameReset = "$(info_prefix) <gray>Your nickname has been reset.</gray>";

    private String nicknameResetOthers = "$(success_prefix) <gray><blue>%s</blue>'s name has been reset.</gray>";

    private String moduleReloaded = "$(success_prefix) <green>Module <blue>%s</blue> has been reloaded.</green>";

    private String socialSpyStatusChanged = "$(info_prefix) <gray>Social spy status has been set to $(socialspy).</gray>";

    private String createdGroup = "$(success_prefix) <green>You've created a new group. You can see the invite code by running <white><click:copy_to_clipboard:'%s'><hover:show_text:'<gray>Click here to copy to clipboard</gray>'>/group code</hover></click></white>. Other players can join the group by using <white>/group join <code></white>.</green>";

    private String groupCode = "$(info_prefix) <gray>Your group's code is <blue>$(group_code)</blue>.</gray>";

    private String joinedGroup = "$(success_prefix) <green>You've joined group <blue>$(group)</blue>.</green>";

    private String leftGroup = "$(success_prefix) <green>You've left group <blue>$(group)</blue>.</green>";

    private String confirmDisbandAction = "$(warning_prefix) <gray>This action cannot be undone! Type <blue>/group disband --confirm</blue> to confirm this action.</gray>";

    private String groupDisbanded = "$(success_prefix) <green>Your group has been disbanded.</green>";

    private String groupAliasChanged = "$(info_prefix) <gray>This group's alias has been set to <blue>$(group)</blue>,</gray>";

    private String userIgnored = "$(success_prefix) <gray>User <blue>%s</blue> has been ignored.</gray>";

    private String userUnignored = "$(success_prefix) <gray>User <blue>%s</blue> has been unignored.</gray>";

    private String userMuted = "$(success_prefix) <gray>User <blue>%s</blue> has been muted in channel <yellow>$(channel)</yellow>.</gray>";

    private String userUnmuted = "$(success_prefix) <gray>User <blue>%s</blue> has been unmuted in channel <yellow>$(channel)</yellow>.</gray>";

    private String userMutedGlobally = "$(success_prefix) <gray>User <blue>%s</blue> has been muted globally.</gray>";

    private String userUnmutedGlobally = "$(success_prefix) <gray>User <blue>%s</blue> has been unmuted globally.</gray>";

    private String processorResult = "$(info_prefix) <blue>Result: </blue>";

    private String processorClickToAnnounce = "$(warning_prefix) <gray>Click here to broadcast this message</gray>";

    private String processorInfoParsers = "$(info_prefix) %s <gray>registered parsers (<white>%s</white> groups)</gray>";

    private String processorInfoParsersByType = "$(info_prefix) :raw_box_up_and_right: <gray>%s</gray>: %s";

}
