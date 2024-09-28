package ovh.mythmc.social.common.text.placeholders;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.SocialPlaceholder;

public final class ClickableNicknamePlaceholder extends SocialPlaceholder {

    @Override
    public String identifier() {
        return "clickable_nickname";
    }

    @Override
    public String process(SocialPlayer player) {
        // We'll return @nickname if PMs are disabled
        if (!Social.get().getConfig().getSettings().getCommands().getPrivateMessage().enabled())
            return "@nickname";

        String hoverText = Social.get().getConfig().getSettings().getChat().getClickableNicknameHoverText();
        if (!player.getNickname().equals(player.getPlayer().getName())) {
            hoverText = hoverText + "\n" + Social.get().getConfig().getSettings().getChat().getPlayerAliasWarningHoverText();
        }

        return "<hover:show_text:'" + hoverText + "'><click:suggest_command:/pm " + player.getPlayer().getName() + " >" + player.getNickname() + "</click></hover>";
    }

}
