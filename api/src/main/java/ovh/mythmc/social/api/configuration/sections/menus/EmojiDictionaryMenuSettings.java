package ovh.mythmc.social.api.configuration.sections.menus;

import java.util.List;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Configuration
@Getter
public class EmojiDictionaryMenuSettings {

    @Comment("The header of this menu")
    private List<String> header = List.of(
        "╒═══════════╕",
        " |         ᴇᴍᴏᴊɪѕ         |",
        "╒═══════════╕"
    );

    @Comment("Max amount of emojis that will be shown per page")
    private int maxEmojisPerPage = 2;

    private String copyToClipboard = "<gray>Click to copy to clipboard</gray>";

    private String category = "<dark_gray>Category</dark_gray>";

    public List<Component> getHeader() {
        return header.stream().map(line -> MiniMessage.miniMessage().deserialize(line)).toList();
    }
    
}
