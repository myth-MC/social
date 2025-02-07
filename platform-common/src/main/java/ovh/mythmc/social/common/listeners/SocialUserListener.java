package ovh.mythmc.social.common.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.LegacySocialSettings;
import ovh.mythmc.social.api.configuration.SocialSettings;
import ovh.mythmc.social.api.events.chat.SocialPrivateMessageEvent;
import ovh.mythmc.social.api.text.parsers.SocialContextualKeyword;
import ovh.mythmc.social.api.users.SocialUser;

import java.util.Collection;
import java.util.UUID;

public final class SocialUserListener implements Listener {

    // Temporary workaround for nicknames
    NamespacedKey key = new NamespacedKey("social", "nickname");

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

        // Temporary workaround for nicknames
        PersistentDataContainer container = user.getPlayer().getPersistentDataContainer();
        if (container.has(key, PersistentDataType.STRING)) {
            String nickname = container.get(key, PersistentDataType.STRING);
            user.getPlayer().setDisplayName(nickname);
            user.getNickname(); // Updates cached nickname
        }

        // Emoji chat completions
        if (Social.get().getConfig().getSettings().getEmojis().isEnabled() && Social.get().getConfig().getGeneral().isChatEmojiTabCompletion())
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
    public void onAdminJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission("social.use.reload"))
            return;

        SocialSettings settings = Social.get().getConfig().getSettings();
        if (settings instanceof LegacySocialSettings legacySettings) {
            if (!legacySettings.isNagAdmins())
                return;

            SocialUser user = Social.get().getUserManager().getByUuid(event.getPlayer().getUniqueId());
            user.sendParsableMessage("$(info_prefix) <yellow>[social] This server is running an outdated settings file! Please, back up and delete your current settings.yml to regenerate a clean setup.</yellow>");   
            user.sendParsableMessage("$(info_prefix) <blue>Hint:</blue> <gray>You can disable this message by setting 'nagAdmins' to false.</gray>");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PersistentDataContainer container = event.getPlayer().getPersistentDataContainer();

        container.set(key, PersistentDataType.STRING, event.getPlayer().getDisplayName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrivateMessage(SocialPrivateMessageEvent event) {
        if (!event.isCancelled())
            Social.get().getChatManager().sendPrivateMessage(event.getSender(), event.getRecipient(), event.getMessage());
    }

}
