package ovh.mythmc.social.common.text.placeholders.player;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parsers.SocialContextualParser;
import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.text.parsers.MiniMessageParser;
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
        SocialUser user = context.user();

        String hoverTextAsString = Social.get().getConfig().getChat().getClickableNicknameHoverText();
        if (!user.getNickname().equals(user.player().get().getName())) {
            hoverTextAsString = hoverTextAsString + "\n" + Social.get().getConfig().getChat().getPlayerAliasWarningHoverText();
        }

        // Todo: temporary workaround
        String commandAsString = Social.get().getConfig().getChat().getClickableNicknameCommand();
        commandAsString = commandAsString.replace("$username", context.user().player().get().getName());
        commandAsString = commandAsString.replace("$(username)", context.user().player().get().getName());

        
        Component hoverText = SocialContextualParser.request(context.withMessage(Component.text(hoverTextAsString)),
            NicknamePlaceholder.class,
            UsernamePlaceholder.class,
            MiniMessageParser.class
        );

        if (commandAsString.isEmpty())
            return Component.text(user.getNickname())
                .hoverEvent(hoverText);

        return Component.text(user.getNickname())
            .clickEvent(ClickEvent.suggestCommand("/" + commandAsString))
            .hoverEvent(hoverText);
    }

}
