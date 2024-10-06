package ovh.mythmc.social.common.text.placeholders.player;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.parsers.SocialPlaceholder;

public final class ClickableNicknamePlaceholder extends SocialPlaceholder {

    @Override
    public String identifier() {
        return "clickable_nickname";
    }

    @Override
    public String process(SocialPlayer player) {
        String hoverText = Social.get().getConfig().getSettings().getChat().getClickableNicknameHoverText();
        if (!player.getNickname().equals(player.getPlayer().getName())) {
            hoverText = hoverText + "\n" + Social.get().getConfig().getSettings().getChat().getPlayerAliasWarningHoverText();
        }

        // Temporary workaround for usernames
        String command = Social.get().getConfig().getSettings().getChat().getClickableNicknameCommand();
        command = command.replace("$username", player.getPlayer().getName());

        return "<click:suggest_command:/" + command + ">"
                + "<hover:show_text:'" + hoverText + "'>"
                + player.getNickname() + "</hover></click>";
    }

}
