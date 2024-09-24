package ovh.mythmc.social.api.emojis;

import java.util.List;

public record Emoji(String name,
                    List<String> aliases,
                    String unicodeCharacter) { }
