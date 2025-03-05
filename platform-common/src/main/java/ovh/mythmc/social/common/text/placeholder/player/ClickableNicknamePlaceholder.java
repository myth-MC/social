package ovh.mythmc.social.common.text.placeholder.player;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.common.text.parser.MiniMessageParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

public final class ClickableNicknamePlaceholder extends SocialContextualPlaceholder {

    @Override
    public boolean legacySupport() {
        return true;
    }

    @Override
    public String identifier() {
        return "clickable_nickname";
    }

    @Override
    public Component get(SocialParserContext context) {
        AbstractSocialUser<?> user = context.user();

        String hoverTextAsString = Social.get().getConfig().getChat().getClickableNicknameHoverText();
        if (!user.cachedName().equals(user.name())) {
            hoverTextAsString = hoverTextAsString + "\n" + Social.get().getConfig().getChat().getPlayerAliasWarningHoverText();
        }

        // Todo: temporary workaround
        String commandAsString = Social.get().getConfig().getChat().getClickableNicknameCommand();
        commandAsString = commandAsString.replace("$username", context.user().name());
        commandAsString = commandAsString.replace("$(username)", context.user().name());

        
        Component hoverText = SocialContextualParser.request(context.withMessage(Component.text(hoverTextAsString)),
            NicknamePlaceholder.class,
            UsernamePlaceholder.class,
            MiniMessageParser.class
        );

        if (commandAsString.isEmpty())
            return user.displayName()
                .hoverEvent(hoverText);

        return user.displayName()
            .clickEvent(ClickEvent.suggestCommand("/" + commandAsString))
            .hoverEvent(hoverText);
    }

}
