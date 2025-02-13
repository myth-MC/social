package ovh.mythmc.social.common.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callbacks.reaction.SocialReactionTrigger;
import ovh.mythmc.social.api.callbacks.reaction.SocialReactionTriggerCallback;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.api.reactions.ReactionFactory;
import ovh.mythmc.social.api.users.SocialUser;

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
            var callback = new SocialReactionTrigger(user, reaction);
            SocialReactionTriggerCallback.INSTANCE.handle(callback, result -> {
                if (!result.cancelled())
                    ReactionFactory.get().displayReaction(result.user(), result.reaction());
            });
        }
    }

}
