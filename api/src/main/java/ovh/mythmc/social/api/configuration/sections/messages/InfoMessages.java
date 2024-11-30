package ovh.mythmc.social.api.configuration.sections.messages;

import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class InfoMessages {

    private String playerJoinedGroup = "$(group_icon) <gray>$(clickable_nickname) has joined this group.</gray>";

    private String playerLeftGroup = "$(group_icon) <gray>$(clickable_nickname) has left this group.</gray>";

    private String groupLeaderChange = "$(group_icon) <gray>$(clickable_nickname) is now the leader of this group.</gray>";

    private String groupDisbanded = "$(group_icon) <gray>This group has been disbanded.</gray>";

}
