package ovh.mythmc.social.api.user;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatParticipant;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.identity.Identified;
import ovh.mythmc.social.api.network.CompanionClient;
import ovh.mythmc.social.api.presence.OnlinePresence;
import ovh.mythmc.social.api.reaction.Reactable;

public interface SocialUser extends
        ChatParticipant,
        Identified,
        CompanionClient,
        OnlinePresence,
        Reactable,
        UserPreferences,
        ForwardingAudience.Single {

    Class<? extends SocialUser> rendererClass();

    boolean checkPermission(@NotNull String permission);

    default void sendParsableMessage(@NotNull SocialParserContext context, boolean playerInput) {
        Component parsedMessage;

        if (playerInput) {
            parsedMessage = Social.get().getTextProcessor().parsePlayerInput(context);
        } else {
            parsedMessage = Social.get().getTextProcessor().parse(context);
        }

        Social.get().getTextProcessor().send(this, parsedMessage, context.messageChannelType(), context.channel());
    }

    default void sendParsableMessage(@NotNull SocialParserContext context) {
        sendParsableMessage(context, false);
    }

    default void sendParsableMessage(@NotNull Component component, boolean playerInput) {
        SocialParserContext context = SocialParserContext.builder(this, component).build();

        sendParsableMessage(context, playerInput);
    }

    default void sendParsableMessage(@NotNull Component component) {
        sendParsableMessage(component, false);
    }

    default void sendParsableMessage(@NotNull String message, boolean playerInput) {
        sendParsableMessage(Component.text(message), playerInput);
    }

    default void sendParsableMessage(@NotNull String message) {
        sendParsableMessage(message, false);
    }

    default @NotNull TextComponent displayNameOrUsername() {
        return displayName().or(Component.text(username()));
    }

}
