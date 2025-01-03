package ovh.mythmc.social.api.configuration.sections.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

import java.util.List;

@Configuration
@Getter
public class ReactionsSettings {

    @Comment("Whether reactions should be enabled or disabled")
    private boolean enabled = true;

    @Comment("Disable this if you don't want reactions to be triggered by trigger words")
    private boolean useTriggerWords = true;

    @Comment("Duration of the reaction in seconds")
    private int durationInSeconds = 3;

    @Comment("Update interval in ticks (20 ticks = 1 second)")
    private int updateIntervalInTicks = 1;

    @Comment("Reaction height offset")
    private double offsetY = 0.6;

    @Comment("You can add your own reactions or remove the default ones too!")
    private List<ReactionField> reactions = List.of(
            new ReactionField("SURPRISED", "https://textures.minecraft.net/texture/9d2920406b136385d2ce35d64f0183ea74ff268368cdf3d02031c06de37c434", "block.note_block.bass", List.of(":o", ":0")),
            new ReactionField("SMILE", "https://textures.minecraft.net/texture/4d8257956e3876da0af97a1117ed814b720b4d045fd52bed85f74113523eacb7", "block.note_block.bell", List.of(":)", "(:")),
            new ReactionField("GRIN", "https://textures.minecraft.net/texture/5059d59eb4e59c31eecf9ece2f9cf3934e45c0ec476fc86bfaef8ea913ea710", "block.chain.break", List.of(":D")),
            new ReactionField("WINK", "https://textures.minecraft.net/texture/f4ea2d6f939fefeff5d122e63dd26fa8a427df90b2928bc1fa89a8252a7e", "block.note_block.flute", List.of(";)")),
            new ReactionField("SAD", "https://textures.minecraft.net/texture/21dff48846d1524273859d717729556f626fa5f2185a1c322e723325263f09c", "block.amethyst_block.break", List.of(":(")),
            new ReactionField("ANGRY", "https://textures.minecraft.net/texture/b1d4bea366aca58dd5b22e940bcdd4ba45bf88421f6d831158b879f2c8abce18", "entity.generic.burn", List.of(">:(")),
            new ReactionField("LAUGHING", "https://textures.minecraft.net/texture/8ad2b4e48959498c3bfb2d149874edbf9a0cfbadceb54ad259aa40706dda1b6c", "item.brush.brushing.sand", List.of("xD")),
            new ReactionField("NEUTRAL", "https://textures.minecraft.net/texture/35a46f8334e49d273384eb72b2ac15e24a640d7648e4b28c348efce93dc97ab", null, List.of(":/", "._.")),
            new ReactionField("HEART", "https://textures.minecraft.net/texture/2869bdd9a8f77eeff75d8f67ed0322bd9c16dd494972314ed707dd10a3139a58", "block.note_block.pling", List.of("<3")),
            new ReactionField("CRYING", "https://textures.minecraft.net/texture/3255846467f33937db9f43cdfb34a0dfd12f57522e8a988e458b5ac3c134e", "block.water.ambient", List.of("T_T", ":'(")),
            new ReactionField("THINKING", "https://textures.minecraft.net/texture/1fea99ad95b570175fda25c3a69788d6a9b854aa13f8a5ff63f6efedf581dfb6", "block.note_block.bit", List.of("hmm"))
    );

    public record ReactionField(String name,
                                String texture,
                                String sound,
                                List<String> triggerWords) { }

}
