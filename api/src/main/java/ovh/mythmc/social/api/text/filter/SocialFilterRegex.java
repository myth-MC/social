package ovh.mythmc.social.api.text.filter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.user.platform.PlatformUsers;

import java.util.regex.Pattern;

public abstract class SocialFilterRegex implements SocialFilterLike {

    public abstract String regex();

    @Override
    public Component parse(SocialParserContext context) {
        if (PlatformUsers.get().checkPermission(context.user(), "social.filter.bypass"))
            return context.message();

        return context.message().replaceText(TextReplacementConfig.builder()
                .match(Pattern.compile(regex()))
                .replacement("***")
                .build());
    }

}
