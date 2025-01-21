package ovh.mythmc.social.api.text.formatters;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.filters.SocialFilterLike;

public abstract class SocialFormatter implements SocialFilterLike {

    public abstract Pattern pattern();

    public abstract Component format(SocialParserContext context);

    protected abstract Component removeFormattingCharacters(Component component);
    
    @Override
    public Component parse(SocialParserContext context) {
        if (!Objects.isNull(context.user().getPlayer()) && !context.user().getPlayer().hasPermission("social.text-formatting"))
            return context.message();

        Component formattedMessage = replace(context);
        List<Component> children = formattedMessage.children().stream()
            .map(child -> replace(context.withMessage(child)))
            .toList();

        return formattedMessage.children(children);
    }

    private Component replace(SocialParserContext context) {
        return context.message().replaceText(TextReplacementConfig.builder()
            .match(pattern())
            .replacement(match -> format(context.withMessage(removeFormattingCharacters(match.build()))))
            .build());
    }

}
