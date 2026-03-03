package ovh.mythmc.social.api.emoji;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a chat emoji that can be inserted into messages.
 *
 * <p>
 * An {@code Emoji} consists of:
 * </p>
 * <ul>
 *     <li>A unique name</li>
 *     <li>Optional aliases</li>
 *     <li>A unicode character representation</li>
 * </ul>
 *
 * <p>
 * Emojis can also generate a formatted {@link Component} description,
 * typically used in menus, hover tooltips, or selection interfaces.
 * </p>
 *
 * <p>
 * Instances are created using the {@link #builder(String, String)} method.
 * </p>
 */
public interface Emoji {

    /**
     * Creates a new {@link Builder} for constructing an {@link Emoji}.
     *
     * @param name             the unique name of the emoji
     * @param unicodeCharacter the unicode character representing the emoji
     * @return a new emoji builder
     */
    static Builder<?> builder(@NotNull String name, @NotNull String unicodeCharacter) {
        return new EmojiImpl.BuilderImpl(name, unicodeCharacter);
    }

    /**
     * Returns the unique name of this emoji.
     *
     * @return the emoji name
     */
    @NotNull String name();

    /**
     * Returns all aliases associated with this emoji.
     *
     * <p>
     * Aliases can be used as alternative identifiers when parsing messages.
     * </p>
     *
     * @return a list of alias strings
     */
    List<String> aliases();

    /**
     * Returns the unicode character representing this emoji.
     *
     * @return the unicode emoji character
     */
    @NotNull String unicodeCharacter();

    /**
     * Creates a formatted description component for this emoji.
     *
     * <p>
     * The returned component is typically used in selection menus or
     * hover tooltips and may optionally include a clickable hint.
     * </p>
     *
     * @param primaryColor      the primary text color
     * @param secondaryColor    the secondary text color
     * @param showClickToInsert whether to include a "click to insert" hint
     * @return a formatted description component
     */
    @NotNull Component asDescription(
            @NotNull TextColor primaryColor,
            @NotNull TextColor secondaryColor,
            boolean showClickToInsert
    );

    /**
     * A builder for constructing {@link Emoji} instances.
     *
     * @param <T> the concrete builder type
     */
    interface Builder<T extends Builder<T>> {

        /**
         * Sets the aliases of the emoji.
         *
         * @param aliases a list of alias strings, or {@code null} for none
         * @return this builder
         */
        @NotNull T aliases(@Nullable List<String> aliases);

        /**
         * Sets the aliases of the emoji.
         *
         * @param aliases alias strings
         * @return this builder
         */
        default @NotNull T aliases(String... aliases) {
            return aliases(Arrays.asList(aliases));
        }

        /**
         * Builds a new {@link Emoji} instance.
         *
         * @return the constructed emoji
         */
        @NotNull Emoji build();
    }
}