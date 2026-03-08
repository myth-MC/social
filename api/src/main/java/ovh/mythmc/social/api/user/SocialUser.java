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

    /**
     * Gets the {@link Class} that {@link ovh.mythmc.social.api.chat.renderer.SocialChatRenderer}
     * will use as a reference to render messages.
     * @return the {@link Class} subtype of {@link SocialUser} to use as a reference for the renderer
     */
    Class<? extends SocialUser> rendererClass();

    /**
     * Checks if this {@link SocialUser} has a specific permission node.
     * @param permission the permission node to check
     * @return           {@code true} if the user has the specific permission node, {@code false}
     *                   otherwise
     */
    boolean checkPermission(@NotNull String permission);

    /**
     * Parses and sends a given message to this {@link SocialUser}.
     * @param context     the {@link SocialParserContext} to parse
     * @param playerInput whether the text processor should use the player input mode
     */
    default void sendParsableMessage(@NotNull SocialParserContext context, boolean playerInput) {
        Component parsedMessage;

        if (playerInput) {
            parsedMessage = Social.get().getTextProcessor().parsePlayerInput(context);
        } else {
            parsedMessage = Social.get().getTextProcessor().parse(context);
        }

        Social.get().getTextProcessor().send(this, parsedMessage, context.messageChannelType(), context.channel());
    }

    /**
     * Parses and sends a given message to this {@link SocialUser}.
     * @param context the {@link SocialParserContext} to parse
     */
    default void sendParsableMessage(@NotNull SocialParserContext context) {
        sendParsableMessage(context, false);
    }

    /**
     * Parses and sends a given message to this {@link SocialUser}.
     * 
     * <p>
     * This method builds a {@link SocialParserContext} with the provided
     * {@link Component}.
     * </p>
     * @param component   the {@link Component} to parse
     * @param playerInput whether the text processor should use the player input mode
     */
    default void sendParsableMessage(@NotNull Component component, boolean playerInput) {
        SocialParserContext context = SocialParserContext.builder(this, component).build();

        sendParsableMessage(context, playerInput);
    }

    /**
     * Parses and sends a given message to this {@link SocialUser}.
     * 
     * <p>
     * This method builds a {@link SocialParserContext} with the provided
     * {@link Component}.
     * </p>
     * @param component   the {@link Component} to parse
     */
    default void sendParsableMessage(@NotNull Component component) {
        sendParsableMessage(component, false);
    }

    /**
     * Parses and sends a given message to this {@link SocialUser}.
     * 
     * <p>
     * This method builds a {@link SocialParserContext} with the provided
     * {@link String} wrapped in a {@link Component}.
     * </p>
     * @param message     the message to parse
     * @param playerInput whether the text processor should use the player input mode
     */
    default void sendParsableMessage(@NotNull String message, boolean playerInput) {
        sendParsableMessage(Component.text(message), playerInput);
    }

    /**
     * Parses and sends a given message to this {@link SocialUser}.
     * 
     * <p>
     * This method builds a {@link SocialParserContext} with the provided
     * {@link String} wrapped in a {@link Component}.
     * </p>
     * @param message     the message to parse
     */
    default void sendParsableMessage(@NotNull String message) {
        sendParsableMessage(message, false);
    }

    /**
     * Gets the display name of this {@link SocialUser} if present, or its
     * username otherwise.
     * @return a {@link TextComponent} with the display name of this user if
     *                                 present or its username otherwise
     */
    default @NotNull TextComponent displayNameOrUsername() {
        return displayName().or(Component.text(username()));
    }

}
