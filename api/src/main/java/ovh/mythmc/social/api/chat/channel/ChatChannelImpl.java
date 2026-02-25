package ovh.mythmc.social.api.chat.channel;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.format.ChatFormatBuilder;
import ovh.mythmc.social.api.chat.renderer.feature.ChatRendererFeature;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.api.util.Mutable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

class ChatChannelImpl implements ChatChannel {

    private final String name;
    private final Mutable<String> alias;
    private final Iterable<String> commands;
    private final Component icon;
    private final Component description;
    private final TextColor color;
    private final ChatFormatBuilder formatBuilder;
    private final Optional<String> permission;
    private final Optional<TextColor> textColor;
    private final boolean joinByDefault;
    private final Collection<ChatRendererFeature> supportedRendererFeatures;

    protected final LinkedHashSet<UUID> memberUuids = new LinkedHashSet<>();

    protected ChatChannelImpl(
        @NotNull String name,
        @NotNull Mutable<String> alias,
        @NotNull Iterable<String> commands,
        @NotNull Component icon,
        @NotNull Component description,
        @NotNull TextColor color,
        @NotNull ChatFormatBuilder formatBuilder,
        @NotNull Optional<String> permission,
        @NotNull Optional<TextColor> textColor,
        boolean joinByDefault,
        @NotNull Collection<ChatRendererFeature> supportedRendererFeatures
    ) {
        this.name = name;
        this.alias = alias;
        this.commands = commands;
        this.icon = icon;
        this.description = description;
        this.color = color;
        this.formatBuilder = formatBuilder;
        this.permission = permission;
        this.textColor = textColor;
        this.joinByDefault = joinByDefault;
        this.supportedRendererFeatures = supportedRendererFeatures;
    }

    @Override
    public @NotNull String name() {
        return name;
    }

    @Override
    public @NotNull Mutable<String> alias() {
        return this.alias;
    }

    @Override
    public @NotNull Iterable<String> commands() {
        return this.commands;
    }

    @Override
    public @NotNull Component icon() {
        return icon;
    }

    @Override
    public @NotNull Component description() {
        return description;
    }

    @Override
    public @NotNull TextColor color() {
        return color;
    }

    protected ChatFormatBuilder formatBuilder() {
        return formatBuilder;
    }

    @Override
    public @NotNull Optional<String> permission() {
        return permission;
    }

    @Override
    public @NotNull Optional<TextColor> textColor() {
        return textColor;
    }

    @Override
    public boolean joinByDefault() {
        return joinByDefault;
    }

    @Override
    public @NotNull Iterable<ChatRendererFeature> supportedRendererFeatures() {
        return supportedRendererFeatures;
    }

    @Override
    public @NotNull Component prefix(@NotNull SocialUser user, @NotNull SocialRegisteredMessageContext message,
            @NotNull SocialParserContext parser) {
        return formatBuilder().preRenderPrefix(user, supportedRendererFeatures(), message, parser);
    }

    @Override
    public boolean addMember(@NotNull UUID uuid) {
        return memberUuids.add(uuid);
    }

    @Override
    public boolean removeMember(@NotNull UUID uuid) {
        return memberUuids.remove(uuid);
    }

    @Override
    public @NotNull List<SocialUser> members() {
        return memberUuids.stream()
                .map(Social.get().getUserService()::getByUuid)
                .map(o -> o.orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public boolean isMember(@NotNull UUID uuid) {
        return memberUuids.contains(uuid);
    }

}
