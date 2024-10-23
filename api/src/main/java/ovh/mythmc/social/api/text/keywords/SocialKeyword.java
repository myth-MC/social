package ovh.mythmc.social.api.text.keywords;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.context.SocialPlayerContext;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.parsers.SocialPlayerInputParser;

import java.util.regex.Pattern;

import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

public abstract class SocialKeyword implements SocialPlayerInputParser {

    public abstract String keyword();

    @Deprecated
    @ScheduledForRemoval
    public abstract String process(SocialPlayer socialPlayer);

    public abstract String process(SocialPlayerContext context);

    @Override
    public Component parse(SocialPlayerContext context) {
        Component processedText = MiniMessage.miniMessage().deserialize(process(context.socialPlayer()));
        return context.message().replaceText(TextReplacementConfig
                .builder()
                .match(Pattern.compile("\\[(?i:" + keyword() + "\\b)\\]"))
                .replacement(processedText)
                .build());
    }

}
