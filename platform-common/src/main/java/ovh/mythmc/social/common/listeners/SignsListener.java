package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import static net.kyori.adventure.text.Component.text;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.users.SocialUser;

public final class SignsListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        SocialUser user = Social.get().getUserManager().getByUuid(event.getPlayer().getUniqueId());
        if (user == null)
            return;

        for (int i = 0; i < event.getLines().length; i++) {
            String line = event.getLine(i);
            String parsedLine = LegacyComponentSerializer.legacySection().serialize(
                    Social.get().getTextProcessor().parsePlayerInput(SocialParserContext.builder().user(user).message(text(line)).build())
            );

            event.setLine(i, parsedLine);
        }
    }

}
