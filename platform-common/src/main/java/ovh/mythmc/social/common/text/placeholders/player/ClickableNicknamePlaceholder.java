package ovh.mythmc.social.common.text.placeholders.player;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.CustomTextProcessor;
import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
        String commandAsString = Social.get().getConfig().getSettings().getChat().getClickableNicknameCommand();
        
        CustomTextProcessor textProcessor = CustomTextProcessor.defaultProcessor()
            .withExclusions(context.appliedParsers());
            
        TextComponent hoverText = (TextComponent) textProcessor.parse(context.withMessage(Component.text(hoverTextAsString)));
        TextComponent command = (TextComponent) textProcessor.parse(context.withMessage(Component.text(commandAsString)));

        if (command.content().equals(""))
            return Component.text(player.getNickname())
                .hoverEvent(HoverEvent.showText(hoverText));

        return Component.text(player.getNickname())
            .clickEvent(ClickEvent.suggestCommand("/" + command.content()))
            .hoverEvent(HoverEvent.showText(hoverText));
    }

}
