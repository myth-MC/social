package ovh.mythmc.social.api.chat.channel;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.format.ChatFormatBuilder;
import ovh.mythmc.social.api.chat.renderer.feature.ChatRendererFeature;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.util.Mutable;

import java.util.*;

class ChatChannelImpl implements ChatChannel {
    
    private final String name;

    private final Mutable<String> alias;

    private final Iterable<String> commands;

    private final Component icon;

    private final Component description;

    private final TextColor color;

    private final ChatFormatBuilder formatBuilder;

    private final String permission;

    private final boolean joinByDefault;

    protected final LinkedHashSet<UUID> memberUuids = new LinkedHashSet<>();

    private final Collection<ChatRendererFeature> supportedRendererFeatures;

    protected ChatChannelImpl(@NotNull String name, @NotNull Mutable<String> alias, @NotNull Iterable<String> commands, @NotNull Component icon, @NotNull Component description, @NotNull TextColor color, @NotNull ChatFormatBuilder formatBuilder, @Nullable String permission, boolean joinByDefault, @NotNull Collection<ChatRendererFeature> supportedRendererFeatures) {
        this.name = name;
        this.alias = alias;
        this.commands = commands;
        this.icon = icon;
        this.description = description;
        this.color = color;
        this.formatBuilder = formatBuilder;
        this.permission = permission;
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
        return Optional.ofNullable(permission);
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
    public @NotNull Component prefix(@NotNull AbstractSocialUser user, @NotNull SocialRegisteredMessageContext message, @NotNull SocialParserContext parser) {
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
    public @NotNull List<AbstractSocialUser> members() {
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

    public static final class ChatChannelBuilder extends Builder<ChatChannelBuilder, ChatChannelImpl> {

        ChatChannelBuilder(@NotNull String name, @NotNull ChatFormatBuilder formatBuilder) {
            super(name, formatBuilder);
        }

        @Override
        public ChatChannelImpl build() {
            return new ChatChannelImpl(name, alias, commands, icon, description, color, formatBuilder, permission, joinByDefault, supportedFeatures);
        }

        @Override
        public ChatChannelBuilder get() {
            return this;
        }
    }

    static abstract class Builder<T extends Builder<T, R>, R extends ChatChannelImpl> {

        protected final String name;

        protected final Mutable<String> alias = Mutable.empty();

        protected final ChatFormatBuilder formatBuilder;

        protected final List<String> commands = new ArrayList<>();

        protected Component icon;

        protected Component description = Component.empty();

        protected TextColor color = NamedTextColor.YELLOW;

        protected String permission;

        protected boolean joinByDefault = false;

        protected final List<UUID> memberUuids = new ArrayList<>();

        protected final Collection<ChatRendererFeature> supportedFeatures = new ArrayList<>();

        protected Builder(@NotNull String name, @NotNull ChatFormatBuilder formatBuilder) {
            this.name = name;
            this.formatBuilder = formatBuilder;
            this.icon = Component.text(name);
        }

        public abstract R build();

        public abstract T get();

        public T alias(@NotNull String alias) {
            get().alias.set(alias);
            return get();
        }

        public T commands(@NotNull String... commands) {
            get().commands.addAll(List.of(commands));
            return get();
        }

        public T icon(@NotNull Component icon) {
            get().icon = icon;
            return get();
        }

        public T description(@NotNull Component description) {
            get().description = description;
            return get();
        }

        public T color(@NotNull TextColor color) {
            get().color = color;
            return get();
        }

        public T permission(@NotNull String permission) {
            get().permission = permission;
            return get();
        }

        public T joinByDefault(boolean joinByDefault) {
            get().joinByDefault = joinByDefault;
            return get();
        }

        public T member(@NotNull UUID uuid) {
            get().memberUuids.add(uuid);
            return get();
        }

        public T member(@NotNull AbstractSocialUser user) {
            return member(user.uuid());
        }

        public T supportedFeature(@NotNull ChatRendererFeature feature) {
            get().supportedFeatures.add(feature);
            return get();
        }

    }
    
}
