package ovh.mythmc.social.api.configuration.section.menus;

import java.util.List;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * Settings for the chat history menu.
 */
@Configuration
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

    public @NotNull List<String> getRawHeader() {
        return header;
    }

    public int getMaxMessagesPerPage() {
        return maxMessagesPerPage;
    }

    public @NotNull String getScope() {
        return scope;
    }

    public @NotNull String getContext() {
        return context;
    }

    public @NotNull String getContextChannel() {
        return contextChannel;
    }

    public @NotNull String getContextReplyTo() {
        return contextReplyTo;
    }

    public @NotNull String getClickToOpenGlobalHistory() {
        return clickToOpenGlobalHistory;
    }

    public @NotNull String getClickToOpenThreadHistory() {
        return clickToOpenThreadHistory;
    }

    public List<Component> getHeader() {
        return header.stream().map(line -> MiniMessage.miniMessage().deserialize(line)).toList();
    }
    
}

