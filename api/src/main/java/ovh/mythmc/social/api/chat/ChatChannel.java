package ovh.mythmc.social.api.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.renderer.feature.ChatRendererFeature;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.text.injection.SocialInjectionParsers;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.user.AbstractSocialUser;

import java.util.*;
import java.util.function.Predicate;

public class ChatChannel {

    public static ChatChannelBuilder builder(@NotNull String name, @NotNull FormatBuilder formatBuilder) {
        return new ChatChannelBuilder(name, formatBuilder);
    }
    
    private final String name;

    private String alias;

    private final Component icon;

    private final Component description;

    private final TextColor color;

    private final FormatBuilder formatBuilder;

    private final String permission;

    private final boolean joinByDefault;

    private final LinkedHashSet<UUID> memberUuids = new LinkedHashSet<>();

    private final Collection<ChatRendererFeature> supportedRendererFeatures;

    protected ChatChannel(@NotNull String name, @Nullable String alias, @NotNull Component icon, @NotNull Component description, @NotNull TextColor color, @NotNull FormatBuilder formatBuilder, @Nullable String permission, boolean joinByDefault, @NotNull Collection<ChatRendererFeature> supportedRendererFeatures) {
        this.name = name;
        this.alias = alias;
        this.icon = icon;
        this.description = description;
        this.color = color;
        this.formatBuilder = formatBuilder;
        this.permission = permission;
        this.joinByDefault = joinByDefault;
        this.supportedRendererFeatures = supportedRendererFeatures;
    }

    public String name() {
        return name;
    }

    public String alias() {
        return alias;
    }

    protected void alias(@NotNull String alias) {
        this.alias = alias;
    }

    public String aliasOrName() {
        return alias() != null ? alias : name;
    }

    public Component icon() {
        return icon;
    }

    public Component description() {
        return description;
    }

    public TextColor color() {
        return color;
    }

    protected FormatBuilder formatBuilder() {
        return formatBuilder;
    }

    public String permission() {
        return permission;
    }

    public boolean joinByDefault() {
        return joinByDefault;
    }

    public Collection<ChatRendererFeature> supportedRendererFeatures() {
        return supportedRendererFeatures;
    }

    public Component prefix(@NotNull AbstractSocialUser user, @NotNull SocialRegisteredMessageContext message, @NotNull SocialParserContext parser) {
        return formatBuilder().preRenderPrefix(user, supportedRendererFeatures(), message, parser);
    }

    public boolean addMember(UUID uuid) {
        return memberUuids.add(uuid);
    }

    public boolean addMember(AbstractSocialUser user) {
        return addMember(user.uuid());
    }

    public boolean removeMember(UUID uuid) {
        return memberUuids.remove(uuid);
    }

    public boolean removeMember(AbstractSocialUser user) {
        return removeMember(user.uuid());
    }

    public List<AbstractSocialUser> members() {
        return memberUuids.stream()
            .map(Social.get().getUserService()::getByUuid)
            .map(o -> o.orElse(null))
            .filter(Objects::nonNull)
            .toList();
    }

    public static final class FormatBuilder {

        public static FormatBuilder empty() {
            return new FormatBuilder();
        }

        public static FormatBuilder from(@NotNull TextComponent base) {
            return new FormatBuilder()
                .append(base);
        }

        private FormatBuilder() { }

        private final List<SocialInjectedValue<?>> injectedValues = new ArrayList<>();

        public FormatBuilder injectValues(@NotNull Iterable<? extends SocialInjectedValue<?>> injectedValues) {
            for (SocialInjectedValue<?> injectedValue : injectedValues) {
                this.injectedValues.add(injectedValue);
            }

            return this;
        }

        public FormatBuilder injectValue(@NotNull SocialInjectedValue<?> injectedValue) {
            this.injectedValues.add(injectedValue);
            return this;
        }

        public FormatBuilder injectValue(@NotNull SocialInjectedValue<?> injectedValue, int index) {
            this.injectedValues.add(index, injectedValue);
            return this;
        }

        public FormatBuilder append(@NotNull TextComponent component, int index) {
            final var injectedValue = SocialInjectedValue.literal(component);
            return injectValue(injectedValue, index);
        }

        public FormatBuilder append(@NotNull TextComponent component) {
            final var injectedValue = SocialInjectedValue.literal(component);
            return injectValue(injectedValue);
        }

        public FormatBuilder appendConditional(@NotNull TextComponent component, @NotNull Predicate<SocialParserContext> predicate, int index) {
            final var injectedValue = SocialInjectedValue.conditional(SocialInjectedValue.literal(component), SocialInjectionParsers.LITERAL, predicate);
            return injectValue(injectedValue, index);
        }

        public FormatBuilder appendConditional(@NotNull TextComponent component, @NotNull Predicate<SocialParserContext> predicate) {
            final var injectedValue = SocialInjectedValue.conditional(SocialInjectedValue.literal(component), SocialInjectionParsers.LITERAL, predicate);
            return injectValue(injectedValue);
        }

        public FormatBuilder appendSpace() {
            return append(Component.space());
        }

        public Component preRenderPrefix(@NotNull AbstractSocialUser target, @NotNull Iterable<? extends ChatRendererFeature> supportedFeatures, @NotNull SocialRegisteredMessageContext message, @NotNull SocialParserContext parser) {
            // Inject values into context
            parser.injectValues(this.injectedValues);

            Component component = Component.empty();

            for (ChatRendererFeature feature : supportedFeatures) {
                if (feature.isApplicable(message))
                    feature.handler().handle(target, this, message, parser);
            }

            for (SocialInjectedValue<?> injectedValue : this.injectedValues) {
                component = injectedValue.parse(parser.withMessage(component));
            }

            return component;
        }

    }

    public static final class ChatChannelBuilder extends Builder<ChatChannelBuilder, ChatChannel> {

        ChatChannelBuilder(@NotNull String name, @NotNull FormatBuilder formatBuilder) {
            super(name, formatBuilder);
        }

        @Override
        public ChatChannel build() {
            return new ChatChannel(name, alias, icon, description, color, formatBuilder, permission, joinByDefault, supportedFeatures);
        }

        @Override
        public ChatChannelBuilder get() {
            return this;
        }
    }

    public static abstract class Builder<T extends Builder<T, R>, R extends ChatChannel> {

        protected final String name;

        protected String alias;

        protected final FormatBuilder formatBuilder;

        protected Component icon;

        protected Component description = Component.empty();

        protected TextColor color = NamedTextColor.YELLOW;

        protected String permission;

        protected boolean joinByDefault = false;

        protected final List<UUID> memberUuids = new ArrayList<>();

        protected final Collection<ChatRendererFeature> supportedFeatures = new ArrayList<>();

        protected Builder(@NotNull String name, @NotNull FormatBuilder formatBuilder) {
            this.name = name;
            this.formatBuilder = formatBuilder;
            this.icon = Component.text(name);
        }

        public abstract R build();

        public abstract T get();

        public T alias(@NotNull String alias) {
            get().alias = alias;
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
