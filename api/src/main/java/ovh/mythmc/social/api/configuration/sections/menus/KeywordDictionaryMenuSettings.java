package ovh.mythmc.social.api.configuration.sections.menus;

import java.util.List;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Configuration
@Getter
public class KeywordDictionaryMenuSettings {

    private List<String> header = List.of(
        "╒═══════════╕",
        " |       ᴋᴇʏᴡᴏʀᴅѕ       |",
        "╒═══════════╕"
    );

    private int maxKeywordsPerPage = 3;

    private String copyToClipboard = "<gray>Click to copy to clipboard</gray>";

    private String result = "<dark_gray>Result</dark_gray>";

    public List<Component> getHeader() {
        return header.stream().map(line -> MiniMessage.miniMessage().deserialize(line)).toList();
    }
    
}
