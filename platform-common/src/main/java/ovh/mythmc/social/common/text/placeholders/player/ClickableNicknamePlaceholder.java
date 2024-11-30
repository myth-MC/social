package ovh.mythmc.social.common.text.placeholders.player;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;
import ovh.mythmc.social.common.text.parsers.MiniMessageParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

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
        SocialPlayer player = context.socialPlayer();

        String hoverTextAsString = Social.get().getConfig().getSettings().getChat().getClickableNicknameHoverText();
        if (!player.getNickname().equals(player.getPlayer().getName())) {
            hoverTextAsString = hoverTextAsString + "\n" + Social.get().getConfig().getSettings().getChat().getPlayerAliasWarningHoverText();
        }

        // Todo: temporary workaround
        String commandAsString = Social.get().getConfig().getSettings().getChat().getClickableNicknameCommand();
        commandAsString = commandAsString.replace("$username", context.socialPlayer().getPlayer().getName());
        commandAsString = commandAsString.replace("$(username)", context.socialPlayer().getPlayer().getName());

        Component hoverText = request(context.withMessage(Component.text(hoverTextAsString)),
            NicknamePlaceholder.class,
            UsernamePlaceholder.class,
            MiniMessageParser.class
        );

        if (commandAsString.isEmpty())
            return Component.text(player.getNickname())
                .hoverEvent(HoverEvent.showText(hoverText));

        return Component.text(player.getNickname())
            .clickEvent(ClickEvent.suggestCommand("/" + commandAsString))
            .hoverEvent(HoverEvent.showText(hoverText));
    }

}
