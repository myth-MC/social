package ovh.mythmc.social.api.configuration.sections.menus;

import java.util.List;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Configuration
@Getter
public class HistoryMenuSettings {

    private List<String> header = List.of(
        "╒═══════════╕",
        " |        ʜɪѕᴛᴏʀʏ        |",
        "╒═══════════╕"
    );

    private int maxMessagesPerPage = 6;

    private String scope = "<dark_gray>Scope:</dark_gray>";

    private String dateFormat = "MM-dd hh:mm";

    private String context = "<blue>Context:</blue>";

    private String channel = "Channel";

    private String replyTo = "Reply to";

    private String clickToOpenGlobalHistory = "<gray>Click here to return to the global history</gray>";

    private String clickToOpenThreadHistory = "<gray>Click here to open this thread history</gray>";

    public List<Component> getHeader() {
        return header.stream().map(line -> MiniMessage.miniMessage().deserialize(line)).toList();
    }
    
}
