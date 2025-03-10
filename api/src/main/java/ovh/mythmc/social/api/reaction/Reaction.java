package ovh.mythmc.social.api.reaction;

import java.util.List;

import net.kyori.adventure.sound.Sound;

public record Reaction(String name,
                       String texture,
                       Sound sound,
                       String particle,
                       List<String> triggerWords) {
}
