package ovh.mythmc.social.sponge.adapter;

import org.spongepowered.api.event.message.PlayerChatEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.sponge.api.SpongeSocialUser;

public final class SpongeChatEventAdapter {

    public void onSubmit(final PlayerChatEvent.Submit event) {
        event.player().ifPresent(player -> {
            // Set variables
            final var sender = SpongeSocialUser.from(player);
            var channel = sender.mainChannel();

            // Flood filter
            if (Social.get().getConfig().getChat().getFilter().isEnabled() && Social.get().getConfig().getChat().getFilter().isFloodFilter()) {
                final int floodFilterCooldownInMilliseconds = Social.get().getConfig().getChat().getFilter().getFloodFilterCooldownInMilliseconds();

                if (System.currentTimeMillis() - sender.latestMessageInMilliseconds() < floodFilterCooldownInMilliseconds &&
                    sender.player().isPresent() && !sender.checkPermission("social.filter.bypass")) {

                    event.setCancelled(true);
                }
            }

            // Mute filter
            if (Social.get().getUserManager().isMuted(sender, channel)) {
                Social.get().getTextProcessor().parseAndSend(sender, channel, Social.get().getConfig().getMessages().getErrors().getCannotSendMessageWhileMuted(), Social.get().getConfig().getMessages().getChannelType());
                event.setCancelled(true);
            }
        });
    }

    public void onDecorate(final PlayerChatEvent.Decorate event) {
        event.player().ifPresent(player -> {
            // Set variables
            final var sender = SpongeSocialUser.from(player);
            var channel = sender.mainChannel();

            // Flood filter
            if (Social.get().getConfig().getChat().getFilter().isEnabled() && Social.get().getConfig().getChat().getFilter().isFloodFilter()) {
                final int floodFilterCooldownInMilliseconds = Social.get().getConfig().getChat().getFilter().getFloodFilterCooldownInMilliseconds();

                if (System.currentTimeMillis() - sender.latestMessageInMilliseconds() < floodFilterCooldownInMilliseconds &&
                    sender.player().isPresent() && !sender.checkPermission("social.filter.bypass")) {

                }
            }
        });
    }

}
