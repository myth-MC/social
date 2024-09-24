package ovh.mythmc.social.api.reactions;

import org.bukkit.Sound;

import java.util.List;

public record Reaction(String name,
                       String texture,
                       Sound sound,
                       List<String> triggerWords) {
}
