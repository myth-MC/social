package ovh.mythmc.social.api.chat.channel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
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

import static net.kyori.adventure.text.Component.text;

@Getter
@Setter(AccessLevel.PROTECTED)
public class SimpleChatChannel extends ChatChannelImpl {

    private final boolean showHoverText;

    private final Component hoverText;

    private final TextColor nicknameColor;

    private final String textDivider;

    private final TextColor textColor;

    protected SimpleChatChannel(@NotNull String name,
                                @Nullable String alias,
                                @NotNull TextColor color,
                                @NotNull Iterable<String> commands,
                                @NotNull Component icon,
                                boolean showHoverText,
                                @NotNull Component hoverText,
                                @NotNull TextColor nicknameColor,
                                @NotNull String textDivider,
                                @NotNull TextColor textColor,
                                @Nullable String permission,
                                boolean joinByDefault) {

        super(name, Mutable.of(alias), commands, icon, hoverText, color, ChatFormatBuilder.empty(), permission, joinByDefault, supportedFeatures());
        this.showHoverText = showHoverText;
        this.hoverText = hoverText;
        this.nicknameColor = nicknameColor;
        this.textDivider = textDivider;
        this.textColor = textColor;
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
            .injectValue(SocialInjectedValue.placeholder("channel_text_color", text(this.getTextColor().asHexString())));
    }

    private static Collection<ChatRendererFeature> supportedFeatures() {
        return List.of(
            ChatRendererFeature.replies(2),
            ChatRendererFeature.companion()
        );
    }

    public static SimpleChatChannel fromConfigField(final @NotNull ChatSettings.Channel channelField) {
        final String name = channelField.name();
        final String alias = channelField.alias();
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

        if (channelField.inherit() != null) { // Inherit properties from another channel
            final ChatChannel channel = Social.registries().channels().value(RegistryKey.identified(channelField.inherit())).orElse(null);
            if (channel instanceof SimpleChatChannel inherit) {
                color = inherit.color();
                icon = inherit.icon();
                showHoverText = inherit.isShowHoverText();
                hoverText = inherit.getHoverText();
                nicknameColor = inherit.getNicknameColor();
                textDivider = inherit.getTextDivider();
                textColor = inherit.getTextColor();
                permission = inherit.permission().orElse(null);
                joinByDefault = inherit.joinByDefault();
            }

        }

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
            textColor,
            permission,
            joinByDefault
        );
    }

    static Component getHoverTextAsComponent(List<String> hoverTextList) {
        Component hoverText = Component.empty();

        for (int i = 0; i < hoverTextList.size(); i++) {
            hoverText = hoverText
                .append(Component.text(hoverTextList.get(i)));

            if (i < hoverTextList.size() - 1)
                hoverText = hoverText
                    .appendNewline();
        }

        return hoverText;
    }

}
