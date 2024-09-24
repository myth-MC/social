package ovh.mythmc.social.api.text.filters;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.SocialParser;

import java.util.regex.Pattern;

public abstract class SocialFilterRegex implements SocialParser, SocialFilterLike {

    public abstract String regex();

    @Override
    public Component parse(SocialPlayer socialPlayer, Component message) {
        if (socialPlayer.getPlayer().hasPermission("social.filter.bypass"))
            return message;

        return message.replaceText(TextReplacementConfig
                .builder()
                .match(Pattern.compile(regex()))
                .replacement("***")
                .build());
    }

}
