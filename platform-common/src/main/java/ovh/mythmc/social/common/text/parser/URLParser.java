package ovh.mythmc.social.common.text.parser;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.filter.SocialFilterLike;
import ovh.mythmc.social.common.text.filter.URLFilter;

public final class URLParser implements SocialFilterLike {

    @Override
    public Component parse(SocialParserContext context) {
        if (!context.user().checkPermission("social.text-formatting"))
            return context.message();

        return context.message().replaceText(TextReplacementConfig.builder()
            .match(URLFilter.regexString())
                .replacement((matchResult, builder) ->
                    Component.text(matchResult.group())
                        .color(NamedTextColor.BLUE)
                        .decoration(TextDecoration.UNDERLINED, true)
                        .clickEvent(ClickEvent.openUrl(matchResult.group())))
            .build()
        );
    }

}
