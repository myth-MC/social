package ovh.mythmc.social.common.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.LegacySocialSettings;
import ovh.mythmc.social.api.configuration.SocialSettings;
import ovh.mythmc.social.api.text.parser.SocialContextualKeyword;
import ovh.mythmc.social.api.user.SocialUser;

import java.util.Collection;
import java.util.UUID;

public final class SocialUserListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        SocialUser user = Social.get().getUserManager().getByUuid(uuid);

        if (user == null)
            Social.get().getUserManager().register(uuid);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        SocialUser user = Social.get().getUserManager().getByUuid(event.getPlayer().getUniqueId());
        
        // Update display name
        user.player().get().setDisplayName(user.getCachedDisplayName());

        // Emoji chat completions
        if (Social.get().getConfig().getEmojis().isEnabled() && Social.get().getConfig().getGeneral().isChatEmojiTabCompletion())
            event.getPlayer().addCustomChatCompletions(Social.get().getEmojiManager().getEmojis().stream()
                .map(emoji -> ":" +emoji.name() + ":")
                .toList());

        // Keyword chat completions
        if (Social.get().getConfig().getGeneral().isChatKeywordTabCompletion()) {
            Collection<String> keywords = Social.get().getTextProcessor().getContextualParsers().stream()
                .filter(parser -> parser instanceof SocialContextualKeyword)
                .map(parser -> "[" + ((SocialContextualKeyword) parser).keyword() + "]")
                .toList();

            event.getPlayer().addCustomChatCompletions(keywords);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @SuppressWarnings("deprecation")
    public void onAdminJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission("social.use.reload")) // Checks if user is admin
            return;

        SocialSettings settings = Social.get().getConfig().getSettings();
        if (settings instanceof LegacySocialSettings legacySettings) {
            if (!legacySettings.isNagAdmins())
                return;

            SocialUser user = Social.get().getUserManager().getByUuid(event.getPlayer().getUniqueId());
            user.sendParsableMessage("$(info_prefix) <yellow>This server is running an outdated settings file! Please, back up and delete your current settings.yml to regenerate a clean setup.</yellow>");   
            user.sendParsableMessage("$(info_prefix) <blue>Hint:</blue> <gray>You can disable this message by setting 'nagAdmins' to false.</gray>");
        }
    }

}
