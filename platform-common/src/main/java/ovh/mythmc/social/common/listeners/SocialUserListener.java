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
import ovh.mythmc.social.api.callbacks.message.SocialPrivateMessageSendCallback;
import ovh.mythmc.social.api.configuration.LegacySocialSettings;
import ovh.mythmc.social.api.configuration.SocialSettings;
import ovh.mythmc.social.api.text.parsers.SocialContextualKeyword;
import ovh.mythmc.social.api.users.SocialUser;

import java.util.Collection;
import java.util.UUID;

public final class SocialUserListener implements Listener {

    // Temporary workaround for nicknames
    NamespacedKey key = new NamespacedKey("social", "nickname");

    private static class IdentifierKeys {

        static final String USER_PRIVATE_MESSAGE_SEND = "social:private-message-send";

    }

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
        PersistentDataContainer container = user.player().get().getPersistentDataContainer();
        if (container.has(key, PersistentDataType.STRING)) {
            String nickname = container.get(key, PersistentDataType.STRING);
            user.player().get().setDisplayName(nickname);
            user.getNickname(); // Updates cached nickname
        }

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
    public void onAdminJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission("social.use.reload"))
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PersistentDataContainer container = event.getPlayer().getPersistentDataContainer();

        container.set(key, PersistentDataType.STRING, event.getPlayer().getDisplayName());
    }

    public void registerCallbackHandlers() {
        SocialPrivateMessageSendCallback.INSTANCE.registerHandler(IdentifierKeys.USER_PRIVATE_MESSAGE_SEND, ctx -> {
            if (!ctx.cancelled())
                Social.get().getChatManager().sendPrivateMessage(ctx.sender(), ctx.recipient(), ctx.plainMessage());
        });
    }

    public void unregisterCallbackHandlers() {
        SocialPrivateMessageSendCallback.INSTANCE.unregisterHandlers(IdentifierKeys.USER_PRIVATE_MESSAGE_SEND);
    }

}
