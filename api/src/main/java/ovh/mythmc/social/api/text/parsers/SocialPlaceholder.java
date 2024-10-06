package ovh.mythmc.social.api.text.parsers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.regex.Pattern;

public abstract class SocialPlaceholder implements SocialParser {

    public abstract String identifier();

    public abstract String process(SocialPlayer player);

    @Override
    public Component parse(SocialPlayer player, Component component) {
        Component processedText = MiniMessage.miniMessage().deserialize(process(player));
        return component.replaceText(TextReplacementConfig
                .builder()
                .match(Pattern.compile("\\$(?i:" + identifier() + "\\b)"))
                .replacement(processedText)
                .build());
    }

}
