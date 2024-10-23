package ovh.mythmc.social.api.text.parsers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.context.SocialPlayerContext;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.regex.Pattern;

import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

public abstract class SocialPlaceholder implements SocialParser {

    public abstract String identifier();

    @Deprecated
    @ScheduledForRemoval
    public abstract String process(SocialPlayer player);

    public String process(SocialPlayerContext context) {
        return process(context.socialPlayer());
    }

    public boolean legacySupport() { return false; }

    @Override
    public Component parse(SocialPlayerContext context) {
        Component processedText = MiniMessage.miniMessage().deserialize(process(context.socialPlayer()));
        Component message = context.message();

        // Legacy support (not delimited)
        if (legacySupport()) {
            message = message.replaceText(TextReplacementConfig
                .builder()
                .match(Pattern.compile("\\$(?i:" + identifier() + "\\b)"))
                .replacement(processedText)
                .build());
        }

        return message.replaceText(TextReplacementConfig
                .builder()
                .match(Pattern.compile("\\$\\((?i:" + identifier() + "\\))"))
                .replacement(processedText)
                .build());
    }

}
