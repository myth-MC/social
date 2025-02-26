package ovh.mythmc.social.api.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import ovh.mythmc.social.api.configuration.section.menus.EmojiDictionaryMenuSettings;
import ovh.mythmc.social.api.configuration.section.menus.HistoryMenuSettings;
import ovh.mythmc.social.api.configuration.section.menus.KeywordDictionaryMenuSettings;
import ovh.mythmc.social.api.configuration.section.menus.PlayerInfoMenuSettings;

@Configuration
@Getter
public class SocialMenus {
    
    @Comment("Settings for the emoji dictionary (/social dictionary emojis)")
    private EmojiDictionaryMenuSettings emojiDictionary = new EmojiDictionaryMenuSettings();

    @Comment({"", "Settings for the keyword dictionary (/social dictionary keywords)"})
    private KeywordDictionaryMenuSettings keywordDictionary = new KeywordDictionaryMenuSettings();

    @Comment({"", "Settings for the player information menu (/social info <player>)"})
    private PlayerInfoMenuSettings playerInfo = new PlayerInfoMenuSettings();

    @Comment({"", "Settings for the chat history (/social history)"})
    private HistoryMenuSettings chatHistory = new HistoryMenuSettings();

}
