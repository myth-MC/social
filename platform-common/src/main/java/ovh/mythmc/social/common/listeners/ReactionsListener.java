package ovh.mythmc.social.common.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.events.reactions.SocialReactionCallEvent;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.api.reactions.ReactionFactory;
import ovh.mythmc.social.common.util.SchedulerUtil;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public final class ReactionsListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        SocialPlayer player = Social.get().getPlayerManager().get(event.getPlayer().getUniqueId());
        if (player == null)
            return;

        if (!player.getPlayer().hasPermission("social.command.reaction"))
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
            SocialReactionCallEvent socialReactionCallEvent = new SocialReactionCallEvent(player, reaction);
            SchedulerUtil.runTask(() -> Bukkit.getPluginManager().callEvent(socialReactionCallEvent));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onReactionCall(SocialReactionCallEvent event) {
        if (!event.isCancelled())
            ReactionFactory.get().displayReaction(event.getSocialPlayer(), event.getReaction());
    }

}
