package ovh.mythmc.social.api.chat.channel;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.social.api.chat.format.ChatFormatBuilder;
import ovh.mythmc.social.api.chat.renderer.feature.ChatRendererFeature;
import ovh.mythmc.social.api.util.Mutable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static net.kyori.adventure.text.Component.text;

/**
 * Fluent builder for creating {@link ChatChannel} instances.
 *
 * <p>
 * This type is exposed as part of the public API via
 * {@link ChatChannel#builder(String, ChatFormatBuilder)}.
 */
public final class ChatChannelBuilder {

    private final String name;
    private final ChatFormatBuilder formatBuilder;

    private final Mutable<String> alias = Mutable.empty();
    private final List<String> commands = new ArrayList<>();

    private Component icon;
    private Component description = Component.empty();
    private TextColor color = NamedTextColor.YELLOW;

    private Optional<String> permission = Optional.empty();
    private Optional<TextColor> textColor = Optional.empty();
    private boolean joinByDefault = false;

    private final Collection<ChatRendererFeature> supportedFeatures = new ArrayList<>();

    public ChatChannelBuilder(@NotNull String name, @NotNull ChatFormatBuilder formatBuilder) {
        this.name = name;
        this.formatBuilder = formatBuilder;
        this.icon = text(name);
    }

    public ChatChannelBuilder alias(@NotNull String value) {
        this.alias.set(value);
        return this;
    }

    public ChatChannelBuilder commands(@NotNull String... values) {
        this.commands.addAll(List.of(values));
        return this;
    }

    public ChatChannelBuilder icon(@NotNull Component value) {
        this.icon = value;
        return this;
    }

    public ChatChannelBuilder description(@NotNull Component value) {
        this.description = value;
        return this;
    }

    public ChatChannelBuilder color(@NotNull TextColor value) {
        this.color = value;
        return this;
    }

    public ChatChannelBuilder permission(@Nullable String value) {
        this.permission = Optional.ofNullable(value);
        return this;
    }

    public ChatChannelBuilder textColor(@Nullable TextColor value) {
        this.textColor = Optional.ofNullable(value);
        return this;
    }

    public ChatChannelBuilder joinByDefault(boolean value) {
        this.joinByDefault = value;
        return this;
    }

    public ChatChannelBuilder supportedFeature(@NotNull ChatRendererFeature feature) {
        this.supportedFeatures.add(feature);
        return this;
    }

    public ChatChannel build() {
        return new ChatChannelImpl(
            this.name,
            this.alias,
            this.commands,
            this.icon,
            this.description,
            this.color,
            this.formatBuilder,
            this.permission,
            this.textColor,
            this.joinByDefault,
            this.supportedFeatures
        );
    }
}

