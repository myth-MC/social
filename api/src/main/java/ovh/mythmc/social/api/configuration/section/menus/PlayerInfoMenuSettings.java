package ovh.mythmc.social.api.configuration.section.menus;

import java.util.List;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Configuration
@Getter
public class PlayerInfoMenuSettings {

    @Comment("The header of this menu")
    private List<String> header = List.of(
        "╒═══════════╕",
        " |        ᴘʟᴀʏᴇʀ         |",
        "╒═══════════╕"
    );

    private String alias = "<dark_gray>Alias</dark_gray>";

    private String username = "<dark_gray>Username</dark_gray>";

    private String mainChannel = "<dark_gray>Main Channel</dark_gray>";

    private String messageCount = "<blue>[Messages]</blue>";

    private String visibleChannels = "<blue>[Channels]</blue>";

    private String status = "<blue>[Status]</blue>";

    private String visibleChannelsHoverText = "<gray>Channels visible by this player:</gray>";

    private String clickToSeeMessageHistory = "<gray>Click to see this player's message history</gray>";

    public List<Component> getHeader() {
        return header.stream().map(line -> MiniMessage.miniMessage().deserialize(line)).toList();
    }
    
}
