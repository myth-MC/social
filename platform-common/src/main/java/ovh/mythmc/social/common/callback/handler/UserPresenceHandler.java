package ovh.mythmc.social.common.callback.handler;

import java.util.Collection;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.LegacySocialSettings;
import ovh.mythmc.social.api.configuration.SocialSettings;
import ovh.mythmc.social.api.text.parser.SocialContextualKeyword;
import ovh.mythmc.social.common.adapter.PlatformAdapter;
import ovh.mythmc.social.common.callback.game.UserPresence;
import ovh.mythmc.social.common.callback.game.UserPresenceCallback;

public final class UserPresenceHandler implements SocialCallbackHandler {

    @Override
    @SuppressWarnings("deprecation")
    public void register() {
        UserPresenceCallback.INSTANCE.registerHandler("social:legacy-settings-nagger", ctx -> {
            if (!ctx.type().equals(UserPresence.Type.JOIN))
                return;
                
            ctx.user().ifPresent(user -> {
                if (!user.checkPermission("social.use.reload"))
                    return;

                SocialSettings settings = Social.get().getConfig().getSettings();
                if (settings instanceof LegacySocialSettings legacySettings) {
                    if (!legacySettings.isNagAdmins())
                        return;

                    user.sendParsableMessage("$(info_prefix) <yellow>This server is running an outdated settings file! Please, back up and delete your current settings.yml to regenerate a clean setup.</yellow>");   
                    user.sendParsableMessage("$(info_prefix) <blue>Hint:</blue> <gray>You can disable this message by setting 'nagAdmins' to false.</gray>");
                }
            });
        });

        UserPresenceCallback.INSTANCE.registerHandler("social:completions", ctx -> {
            if (!ctx.type().equals(UserPresence.Type.JOIN))
                return;
            
            ctx.user().ifPresent(user -> {
                // Emoji chat completions
                if (Social.get().getConfig().getEmojis().isEnabled() && Social.get().getConfig().getGeneral().isChatEmojiTabCompletion())
                    PlatformAdapter.get().sendAutoCompletions(user, Social.get().getEmojiManager().getEmojis().stream()
                        .map(emoji -> ":" + emoji.name() + ":")
                        .toList());

                // Keyword chat completions
                if (Social.get().getConfig().getGeneral().isChatKeywordTabCompletion()) {
                    Collection<String> keywords = Social.get().getTextProcessor().getContextualParsers().stream()
                        .filter(parser -> parser instanceof SocialContextualKeyword)
                        .map(parser -> "[" + ((SocialContextualKeyword) parser).keyword() + "]")
                        .toList();

                    PlatformAdapter.get().sendAutoCompletions(user, keywords);
                }
            });
        });

        UserPresenceCallback.INSTANCE.registerHandler("social:name-updater", ctx -> {
            if (!ctx.type().equals(UserPresence.Type.JOIN))
                return;

            ctx.user().ifPresent(user -> {
                String cachedName = user.cachedName();
                if (cachedName != null)
                    user.name(user.cachedName());
            });
        });
    }

    @Override
    public void unregister() {
        UserPresenceCallback.INSTANCE.unregisterHandlers(
            "social:legacy-settings-nagger",
            "social:completions",
            "social:name-updater"
        );
    }
    
}
