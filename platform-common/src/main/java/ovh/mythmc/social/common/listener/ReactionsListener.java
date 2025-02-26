package ovh.mythmc.social.common.listener;

import lombok.RequiredArgsConstructor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.reaction.Reaction;
import ovh.mythmc.social.api.reaction.ReactionFactory;
import ovh.mythmc.social.api.user.SocialUser;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public final class ReactionsListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        SocialUser user = Social.get().getUserManager().getByUuid(event.getPlayer().getUniqueId());
        if (user == null)
            return;

        Reaction reaction = null;
        for (Reaction r : Social.get().getReactionManager().getReactionsMap().keySet()) {
            if (r.triggerWords() == null || r.triggerWords().isEmpty())
                continue;

            for (String triggerWord : r.triggerWords()) {
                if (event.getMessage().matches("(?i:" + Pattern.quote(triggerWord) + ")")) {
                    reaction = r;
                }
            }
        }

        if (reaction != null) {
            ReactionFactory.get().scheduleReaction(user, reaction);
        }
    }

}
