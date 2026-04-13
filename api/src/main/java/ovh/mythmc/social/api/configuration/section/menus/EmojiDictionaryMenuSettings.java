package ovh.mythmc.social.api.configuration.section.menus;

import java.util.List;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * Settings for the emoji dictionary menu.
 */
@Configuration
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

    public @NotNull List<String> getRawHeader() {
        return header;
    }

    public int getMaxEmojisPerPage() {
        return maxEmojisPerPage;
    }

    public @NotNull String getCopyToClipboard() {
        return copyToClipboard;
    }

    public @NotNull String getCategory() {
        return category;
    }

    public List<Component> getHeader() {
        return header.stream().map(line -> MiniMessage.miniMessage().deserialize(line)).toList();
    }
    
}

