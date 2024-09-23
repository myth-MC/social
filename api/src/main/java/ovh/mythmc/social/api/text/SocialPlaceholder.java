package ovh.mythmc.social.api.text;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.players.SocialPlayer;

import static net.kyori.adventure.text.Component.text;

public abstract class SocialPlaceholder implements SocialParser {

    public abstract String identifier();

    public abstract String process(SocialPlayer player);

    @Override
    public Component parse(SocialPlayer player, Component component) {
        return component.replaceText(identifier(), text(process(player)));
    }

}
