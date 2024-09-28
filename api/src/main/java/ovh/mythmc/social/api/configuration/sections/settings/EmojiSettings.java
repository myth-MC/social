package ovh.mythmc.social.api.configuration.sections.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

import java.util.List;

@Configuration
@Getter
public class EmojiSettings {

    @Comment("Whether the emoji module should be enabled or disabled")
    private boolean enabled = true;

    @Comment("You can add your own emojis or remove the default ones too")
    private List<EmojiField> emojis = List.of(
            new EmojiField("airplane", List.of(), "\u2708"),
            new EmojiField("arrow", List.of(), "\u27A1"),
            new EmojiField("ballot_box", List.of(), "\u2610"),
            new EmojiField("ballot_box_with_x", List.of(), "\u2612"),
            new EmojiField("bow", List.of(), "\uD83C\uDFF9"),
            new EmojiField("checkmark", List.of(), "\u2714"),
            new EmojiField("check_box_with_check", List.of(), "\u2611"),
            new EmojiField("cloud", List.of(), "\u2601"),
            new EmojiField("comet", List.of(), "\u2604"),
            new EmojiField("copyright", List.of(), "\u00A9"),
            new EmojiField("envelope", List.of("mail"), "\u2709"),
            new EmojiField("frowning_face", List.of("frowning", "sad"), "\u2639"),
            new EmojiField("heart", List.of("<3"), "\u2764"),
            new EmojiField("heart_exclamation", List.of(), "\u2763"),
            new EmojiField("hourglass", List.of(), "\u231B"),
            new EmojiField("multiply", List.of("x"), "\u2716"),
            new EmojiField("music", List.of(), "\u266A"),
            new EmojiField("peace", List.of(), "\u262E"),
            new EmojiField("pencil", List.of(), "\u270E"),
            new EmojiField("play", List.of("divider"), "\u25B6"),
            new EmojiField("registered", List.of(), "\u00AE"),
            new EmojiField("reverse", List.of(), "\u25C0"),
            new EmojiField("scissors", List.of(), "\u2702"),
            new EmojiField("shield", List.of(), "\uD83D\uDEE1"),
            new EmojiField("skull", List.of(), "\u2620"),
            new EmojiField("smiling_face", List.of("smiling", "smile"), "\u263A"),
            new EmojiField("snowflake", List.of(), "\u2744"),
            new EmojiField("snowman", List.of(), "\u2603"),
            new EmojiField("star", List.of(), "\u2605"),
            new EmojiField("sun", List.of(), "\u2600"),
            new EmojiField("trademark", List.of("tm", "trade_mark"), "\u2122"),
            new EmojiField("umbrella", List.of(), "\u2602"),
            new EmojiField("warning", List.of(), "\u26A0"),
            new EmojiField("watch", List.of("clock"), "\u231A"),
            new EmojiField("ying_yang", List.of(), "\u262F")
    );

    public record EmojiField(String name,
                             List<String> aliases,
                             String unicodeCharacter) { }

}
