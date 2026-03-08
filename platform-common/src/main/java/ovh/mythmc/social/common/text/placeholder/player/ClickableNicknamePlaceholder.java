package ovh.mythmc.social.common.text.placeholder.player;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;
import ovh.mythmc.social.api.user.SocialUser;
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
        final SocialUser user = context.user();

        String hoverTextAsString = Social.get().getConfig().getChat().getClickableNicknameHoverText();
        if (!user.displayNameOrUsername().content().equals(user.username())) {
            hoverTextAsString = hoverTextAsString + "\n"
                    + Social.get().getConfig().getChat().getPlayerAliasWarningHoverText();
        }

        // Todo: temporary workaround
        String commandAsString = Social.get().getConfig().getChat().getClickableNicknameCommand();
        commandAsString = commandAsString.replace("$username", context.user().username());
        commandAsString = commandAsString.replace("$(username)", context.user().username());

        Component hoverText = SocialContextualParser.request(context.withMessage(Component.text(hoverTextAsString)),
                NicknamePlaceholder.class,
                UsernamePlaceholder.class,
                MiniMessageParser.class);

        if (commandAsString.isEmpty())
            return user.displayNameOrUsername()
                    .hoverEvent(hoverText);

        return user.displayNameOrUsername()
                .clickEvent(ClickEvent.suggestCommand("/" + commandAsString))
                .hoverEvent(hoverText);
    }

}
