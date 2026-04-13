package ovh.mythmc.social.api.chat.channel;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.format.ChatFormatBuilder;
import ovh.mythmc.social.api.chat.renderer.feature.ChatRendererFeature;
import ovh.mythmc.social.api.configuration.section.settings.ChatSettings;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.util.Mutable;
import ovh.mythmc.social.api.util.registry.RegistryKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static net.kyori.adventure.text.Component.text;

/**
 * A simple implementation of a chat channel.
 */
public class SimpleChatChannel extends ChatChannelImpl {

    private final boolean showHoverText;
    private final Component hoverText;
    private final TextColor nicknameColor;
    private final String textDivider;

    protected SimpleChatChannel(@NotNull String name,
                                @Nullable String alias,
                                @NotNull TextColor color,
                                @NotNull Iterable<String> commands,
                                @NotNull Component icon,
                                boolean showHoverText,
                                @NotNull Component hoverText,
                                @NotNull TextColor nicknameColor,
                                @NotNull String textDivider,
                                @Nullable String permission,
                                @Nullable TextColor textColor,
                                boolean joinByDefault) {

        super(
            name,
            Mutable.of(alias),
            commands,
            icon,
            hoverText,
            color,
            ChatFormatBuilder.empty(),
            Optional.ofNullable(permission),
            Optional.ofNullable(textColor),
            joinByDefault,
            supportedFeatures()
        );
        this.showHoverText = showHoverText;
        this.hoverText = hoverText;
        this.nicknameColor = nicknameColor;
        this.textDivider = textDivider;
    }

    /**
     * Checks if hover text should be shown for this channel.
     *
     * @return True if hover text should be shown.
     */
    public boolean isShowHoverText() {
        return showHoverText;
    }

    /**
     * Gets the hover text for this channel.
     *
     * @return The hover text component.
     */
    public @NotNull Component getHoverText() {
        return hoverText;
    }

    /**
     * Gets the color used for nicknames in this channel.
     *
     * @return The nickname color.
     */
    public @NotNull TextColor getNicknameColor() {
        return nicknameColor;
    }

    /**
     * Gets the text divider used in this channel's format.
     *
     * @return The text divider.
     */
    public @NotNull String getTextDivider() {
        return textDivider;
    }

    @Override
    protected ChatFormatBuilder formatBuilder() {
        return ChatFormatBuilder.empty()
            .append(text("$(clickable_channel_icon)"))
            .appendSpace()
            .append(text("$(formatted_nickname)"))
            .appendSpace()
            .append(text("$(channel_text_divider)"))
            .appendSpace()
            .append(text("<$(channel_text_color)>"))
            .injectValue(SocialInjectedValue.placeholder("channel_text_divider", text(this.getTextDivider())))
            .injectValue(SocialInjectedValue.placeholder("channel_text_color", text(this.textColor().orElse(NamedTextColor.WHITE).asHexString())));
    }

    private static Collection<ChatRendererFeature> supportedFeatures() {
        return List.of(
            ChatRendererFeature.replies(2),
            ChatRendererFeature.companion()
        );
    }

    /**
     * Creates a {@link SimpleChatChannel} from a configuration field.
     *
     * @param channelField The configuration field.
     * @return The created chat channel.
     */
    public static @NotNull SimpleChatChannel fromConfigField(final @NotNull ChatSettings.Channel channelField) {
        final String name = channelField.name();
        final String alias = channelField.alias();
        
        // Defaults
        TextColor color = NamedTextColor.YELLOW;
        final List<String> commands = new ArrayList<>();
        Component icon = Component.text("<dark_gray>[<yellow>:raw_pencil:</yellow>]</dark_gray>");
        boolean showHoverText = false;
        Component hoverText = Component.empty();
        TextColor nicknameColor = NamedTextColor.GRAY;
        String textDivider = "<gray>:raw_divider:</gray>";
        TextColor textColor = NamedTextColor.WHITE;
        String permission = null;
        boolean joinByDefault = false;

        // Inherit properties if specified
        if (channelField.inherit() != null) {
            final ChatChannel channel = Social.registries().channels().value(RegistryKey.identified(channelField.inherit())).orElse(null);
            if (channel instanceof SimpleChatChannel inherit) {
                color = inherit.color();
                icon = inherit.icon();
                showHoverText = inherit.isShowHoverText();
                hoverText = inherit.getHoverText();
                nicknameColor = inherit.getNicknameColor();
                textDivider = inherit.getTextDivider();
                textColor = inherit.textColor().orElse(null);
                permission = inherit.permission().orElse(null);
                joinByDefault = inherit.joinByDefault();
            }
        }

        // Apply overrides from config
        if (channelField.color() != null)
            color = TextColor.fromHexString(channelField.color());

        if (channelField.commands() != null)
            commands.addAll(channelField.commands());

        if (channelField.icon() != null)
            icon = Component.text(channelField.icon());

        if (channelField.showHoverText() != null)
            showHoverText = channelField.showHoverText();

        if (channelField.hoverText() != null)
            hoverText = getHoverTextAsComponent(channelField.hoverText());

        if (channelField.nicknameColor() != null)
            nicknameColor = TextColor.fromHexString(channelField.nicknameColor());

        if (channelField.textDivider() != null)
            textDivider = channelField.textDivider();

        if (channelField.textColor() != null)
            textColor = TextColor.fromHexString(channelField.textColor());

        if (channelField.permission() != null)
            permission = channelField.permission();

        if (channelField.joinByDefault() != null)
            joinByDefault = channelField.joinByDefault();

        return new SimpleChatChannel(
            name,
            alias,
            color,
            commands,
            icon,
            showHoverText,
            hoverText,
            nicknameColor,
            textDivider,
            permission,
            textColor,
            joinByDefault
        );
    }

    /**
     * Converts a list of strings to a single component joined by newlines.
     *
     * @param hoverTextList The list of strings.
     * @return The joined component.
     */
    static @NotNull Component getHoverTextAsComponent(@NotNull List<String> hoverTextList) {
        return Component.join(
                JoinConfiguration.newlines(),
                hoverTextList.stream().map(Component::text).toList()
        );
    }

}

