package ovh.mythmc.social.api.configuration.section.menus;

import java.util.List;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Configuration
@Getter
public class HistoryMenuSettings {

    @Comment("The header of this menu")
    private List<String> header = List.of(
        "╒═══════════╕",
        " |        ʜɪѕᴛᴏʀʏ        |",
        "╒═══════════╕"
    );

    @Comment("Max amount of messages that will be shown per page")
    private int maxMessagesPerPage = 6;

    @Comment("Text that will be used to display the current history scope")
    private String scope = "<dark_gray>Scope:</dark_gray>";

    @Comment("Text that will be used to display a message's context")
    private String context = "<blue>Context:</blue>";

    private String contextChannel = "Channel";

    private String contextReplyTo = "Reply to";

    private String clickToOpenGlobalHistory = "<gray>Click here to return to the global history</gray>";

    private String clickToOpenThreadHistory = "<gray>Click here to open this thread</gray>";

    public List<Component> getHeader() {
        return header.stream().map(line -> MiniMessage.miniMessage().deserialize(line)).toList();
    }
    
}
