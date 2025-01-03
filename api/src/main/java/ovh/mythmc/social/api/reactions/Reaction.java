package ovh.mythmc.social.api.reactions;

import java.util.List;

import net.kyori.adventure.sound.Sound;

public record Reaction(String name,
                       String texture,
                       Sound sound,
                       List<String> triggerWords) {
}
