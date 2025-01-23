package ovh.mythmc.social.common.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.events.reactions.SocialReactionCallEvent;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.api.reactions.ReactionFactory;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.util.PluginUtil;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public final class ReactionsListener implements Listener {

    private final JavaPlugin plugin;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        SocialUser user = Social.get().getUserManager().get(event.getPlayer().getUniqueId());
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
            SocialReactionCallEvent socialReactionCallEvent = new SocialReactionCallEvent(user, reaction);
            PluginUtil.runGlobalTask(plugin, () -> Bukkit.getPluginManager().callEvent(socialReactionCallEvent));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onReactionCall(SocialReactionCallEvent event) {
        if (!event.isCancelled())
            ReactionFactory.get().displayReaction(event.getUser(), event.getReaction());
    }

}
