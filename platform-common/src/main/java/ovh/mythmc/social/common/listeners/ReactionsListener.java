package ovh.mythmc.social.common.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.api.reactions.ReactionFactory;

@RequiredArgsConstructor
public final class ReactionsListener implements Listener {

    private final JavaPlugin plugin;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        SocialPlayer player = Social.get().getPlayerManager().get(event.getPlayer().getUniqueId());
        if (player == null)
            return;

        if (!player.getPlayer().hasPermission("social.command.reaction"))
            return;

        Reaction reaction = null;
        for (Reaction r : Social.get().getReactionManager().getReactions()) {
            if (r.triggerWords() == null)
                continue;

            for (String triggerWord : r.triggerWords()) {
                if (event.getMessage().contains(triggerWord)) {
                    reaction = r;
                }
            }
        }

        if (reaction != null) {
            Reaction finalReaction = reaction;
            Bukkit.getScheduler().runTask(plugin, () -> ReactionFactory.get().displayReaction(player, finalReaction));
        }
    }

}
