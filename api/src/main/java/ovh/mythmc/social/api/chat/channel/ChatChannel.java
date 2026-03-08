package ovh.mythmc.social.api.chat.channel;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.format.ChatFormatBuilder;
import ovh.mythmc.social.api.chat.renderer.feature.ChatRendererFeature;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.api.util.Mutable;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a chat channel that players can join, leave, and communicate in.
 *
 * <p>
 * Channels are registered via
 * {@link ovh.mythmc.social.api.SocialRegistries.Channels}.
 * Use {@link #builder(String, ChatFormatBuilder)} to construct new instances.
 */
public interface ChatChannel {

    /**
     * Creates a new {@link ChatChannel} builder.
     *
     * @param name          the unique identifier name for the channel
     * @param formatBuilder the format builder that defines the channel prefix
     * @return a new builder instance
     */
    static ChatChannelBuilder builder(@NotNull String name, @NotNull ChatFormatBuilder formatBuilder) {
        return new ChatChannelBuilder(name, formatBuilder);
    }

    /** Returns the unique name that identifies this channel. */
    @NotNull
    String name();

    /** Returns the mutable alias for this channel, if any. */
    @NotNull
    Mutable<String> alias();

    /**
     * Returns the alias if one is present, otherwise the channel {@link #name()}.
     *
     * @return alias or name
     */
    default String aliasOrName() {
        return alias().isPresent() ? alias().get() : name();
    }

    /** Returns the slash commands that switch a player into this channel. */
    @NotNull
    Iterable<String> commands();

    /** Returns the icon component shown in menus for this channel. */
    @NotNull
    Component icon();

    /** Returns a short description of this channel shown in menus. */
    @NotNull
    Component description();

    /** Returns the primary colour associated with this channel. */
    @NotNull
    TextColor color();

    /**
     * Returns the permission node required to see or write in this channel, if any.
     */
    @NotNull
    Optional<String> permission();

    /**
     * Returns an optional override colour applied to message text in this channel.
     */
    @NotNull
    Optional<TextColor> textColor();

    /**
     * Returns {@code true} if players are automatically added to this channel on
     * join.
     */
    boolean joinByDefault();

    /**
     * Returns the renderer features that are active in this channel (e.g. replies,
     * reactions).
     */
    @NotNull
    Iterable<ChatRendererFeature> supportedRendererFeatures();

    /**
     * Builds the rendered prefix shown before each message in this channel.
     *
     * @param user    the message sender
     * @param message the registered message context
     * @param parser  the parser context used for placeholder resolution
     * @return the rendered prefix component
     */
    @NotNull
    Component prefix(@NotNull SocialUser user, @NotNull SocialRegisteredMessageContext message,
            @NotNull SocialParserContext parser);

    /**
     * Adds the player with the given UUID to this channel.
     *
     * @param uuid the player's UUID
     * @return {@code true} if the player was not already a member
     */
    boolean addMember(@NotNull UUID uuid);

    /**
     * Adds the given user to this channel.
     *
     * @param user the user to add
     * @return {@code true} if the user was not already a member
     */
    default boolean addMember(@NotNull SocialUser user) {
        return addMember(user.uuid());
    }

    /**
     * Removes the player with the given UUID from this channel.
     *
     * @param uuid the player's UUID
     * @return {@code true} if the player was a member
     */
    boolean removeMember(@NotNull UUID uuid);

    /**
     * Removes the given user from this channel.
     *
     * @param user the user to remove
     * @return {@code true} if the user was a member
     */
    default boolean removeMember(@NotNull SocialUser user) {
        return removeMember(user.uuid());
    }

    /**
     * Returns all users currently in this channel.
     *
     * @return the current members
     */
    @NotNull
    Collection<SocialUser> members();

    /**
     * Returns {@code true} if the player with the given UUID is a member of this
     * channel.
     *
     * @param uuid the player UUID to check
     * @return {@code true} if the player is a member
     */
    boolean isMember(@NotNull UUID uuid);

    /**
     * Returns {@code true} if the given user is a member of this channel.
     *
     * @param user the user to check
     * @return {@code true} if the user is a member
     */
    default boolean isMember(@NotNull SocialUser user) {
        return isMember(user.uuid());
    }

    /**
     * Determines how a message is delivered to the recipients of this channel.
     */
    enum ChannelType {
        /** Delivered as a regular chat message. */
        CHAT,
        /** Delivered to the action bar area above the hotbar. */
        ACTION_BAR
    }
}
