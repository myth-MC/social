package ovh.mythmc.social.common.text.parsers;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.text.SocialParser;
import ovh.mythmc.social.api.players.SocialPlayer;

@Getter
public final class PlaceholderAPIParser extends PlaceholderExpansion implements SocialParser {

    private final String identifier = "social";
    private final String author = "myth-MC";
    private final String version = Social.get().version();

    public PlaceholderAPIParser() {
        // this.register();
        // ^ not necessary for now

        Social.get().getLogger().info("Registered internal PlaceholderAPI adapter");
        Social.get().getTextProcessor().registerParser(this);
    }

    @Override
    public Component parse(SocialPlayer socialPlayer, Component message) {
        String serialized = MiniMessage.miniMessage().serialize(message);
        String parsedMessage = PlaceholderAPI.setPlaceholders(socialPlayer.getPlayer(), serialized);
        return MiniMessage.miniMessage().deserialize(parsedMessage);
    }

}
