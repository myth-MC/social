package ovh.mythmc.social.api.configuration.section.menus;

import java.util.List;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Configuration
@Getter
public class KeywordDictionaryMenuSettings {

    @Comment("The header of this menu")
    private List<String> header = List.of(
        "╒═══════════╕",
        " |       ᴋᴇʏᴡᴏʀᴅѕ       |",
        "╒═══════════╕"
    );

    @Comment("Max amount of keywords that will be shown per page")
    private int maxKeywordsPerPage = 3;

    private String copyToClipboard = "<gray>Click to copy to clipboard</gray>";

    private String result = "<dark_gray>Result</dark_gray>";

    public List<Component> getHeader() {
        return header.stream().map(line -> MiniMessage.miniMessage().deserialize(line)).toList();
    }
    
}
