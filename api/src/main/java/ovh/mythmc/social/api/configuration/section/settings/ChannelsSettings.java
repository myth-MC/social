package ovh.mythmc.social.api.configuration.section.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.text.injection.SocialInjectionParsers;
import ovh.mythmc.social.api.text.injection.conditional.SocialInjectedConditionalValue;
import ovh.mythmc.social.api.text.injection.value.AbstractSocialInjectedValue;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedLiteral;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedPlaceholder;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/*
    Unfinished draft for 'builder' chat channels
 */
@Configuration
@Getter
public class ChannelsSettings {

    @Comment("Global injections. These injections can be used by any channel builder")
    private List<ConfiguredInjectableValue> globalInjections = List.of(
        new ConfiguredInjectableValue(ConfiguredInjectableValue.Type.PLACEHOLDER, "example-injected-placeholder", Component.text("<gray>Injection rocks</gray>"))
    );

    @Comment("Configured channels. You can easily add your own or remove the built-in ones")
    private List<BuildableChannel> channels = List.of(
        new BuildableChannel(
            "global",
            Component.text("<dark_gray>[<yellow>:raw_pencil:</yellow>]</dark_gray>"),
            Component.text("This is the global channel"),
            "#FFFF55",
            null,
            null,
            List.of(
                new BuildableComponent(
                    BuildableComponent.Type.LITERAL,
                    Component.text("$(channel_icon)"),
                    null
                ),
                new BuildableComponent(
                    BuildableComponent.Type.SPACE,
                    null,
                    null
                ),
                new BuildableComponent(
                    BuildableComponent.Type.LITERAL,
                    Component.text("$(reply_icon)"),
                    BuildableComponent.Condition.IS_REPLY
                ),
                new BuildableComponent(
                    BuildableComponent.Type.SPACE,
                    null,
                    BuildableComponent.Condition.IS_REPLY
                ),
                new BuildableComponent(
                    BuildableComponent.Type.LITERAL,
                    Component.text("$(formatted_nickname)"),
                    null
                ),
                new BuildableComponent(
                    BuildableComponent.Type.SPACE,
                    null,
                    null
                ),
                new BuildableComponent(
                    BuildableComponent.Type.LITERAL,
                    Component.text("$(channel_divider)"),
                    null
                )
            )
        )
    );

    public record ConfiguredInjectableValue(@NotNull Type type,
                                @Nullable String identifier,
                                @NotNull TextComponent value) {

        public SocialInjectedValue<?> toValue() {
            switch (type) {
                case PLACEHOLDER -> {
                    return SocialInjectedValue.placeholder(Objects.requireNonNull(identifier), value);
                }
                case LITERAL -> {
                    return SocialInjectedValue.literal(value);
                }
                default -> {
                    return null;
                }
            }
        }

        public enum Type {
            PLACEHOLDER,
            LITERAL,
            TAG
        }

    }

    public record BuildableChannel(@NotNull String name,
                                   @NotNull TextComponent icon,
                                   @NotNull TextComponent description,
                                   @NotNull String color,
                                   @Nullable String inherit,
                                   @Nullable List<ConfiguredInjectableValue> injections,
                                   @Nullable List<BuildableComponent> builder) {

        public ChatChannel toChannel() {
            return ChatChannel.builder(name, toFormatBuilder())
                .icon(icon)
                .description(description)
                .color(Objects.requireNonNull(TextColor.fromHexString(color)))
                .build();
        }

        public ChatChannel.FormatBuilder toFormatBuilder() {
            final List<SocialInjectedValue<?>> injectedValues = new ArrayList<>();
            if (injections != null) {
                for (ConfiguredInjectableValue injectedValue : injections) {
                    injectedValues.add(injectedValue.toValue());
                }
            }

            // Default injected placeholders
            injectedValues.add(SocialInjectedPlaceholder.of("channel_name", Component.text(name)));
            injectedValues.add(SocialInjectedPlaceholder.of("channel_color", Component.text(color)));
            injectedValues.add(SocialInjectedPlaceholder.of("channel_icon", icon));

            AtomicReference<List<BuildableComponent>> buildableComponent = new AtomicReference<>(builder);

            // Inherit values
            if (inherit != null) {
                final var inheritChannel = getSuperInherit();

                if (inheritChannel != null) {
                    if (inheritChannel.injections != null) {
                        for (ConfiguredInjectableValue injectableValue : inheritChannel.injections) {
                            injectedValues.add(injectableValue.toValue());
                        }
                    }

                    if (inheritChannel.builder != null) {
                        buildableComponent.set(inheritChannel.builder);
                    }
                }

            }

            final List<? extends AbstractSocialInjectedValue<?>> injectableBuilder = buildableComponent.get().stream()
                .map(BuildableComponent::toInjectableValue)
                .toList();

            return ChatChannel.FormatBuilder.empty()
                .injectValues(injectableBuilder)
                .injectValues(injectedValues);
        }

        public BuildableChannel getSuperInherit() {
            BuildableChannel buildableChannel = this;
            boolean end = false;

            while (!end) {
                if (buildableChannel.inherit != null) {
                    /*
                    final var inheritChannel = Social.get().getConfig().getChannels().getChannels().stream()
                        .filter(configuredChannel -> configuredChannel.name().equals(inherit))
                        .findFirst();

                    if (inheritChannel.isPresent()) {
                        buildableChannel = inheritChannel.get();
                    } else {
                        end = true;
                    }

                     */
                }
            }

            return buildableChannel;
        }

    }

    public record BuildableComponent(@NotNull Type type, @Nullable TextComponent text, @Nullable Condition condition) {

        public AbstractSocialInjectedValue<?> toInjectableValue() {
            SocialInjectedLiteral literal = SocialInjectedLiteral.of(Component.empty());
            switch (type) {
                case LITERAL -> literal = SocialInjectedLiteral.of(text);
                case SPACE -> literal = SocialInjectedLiteral.of(Component.space());
            }

            if (condition == null)
                return literal;

            return SocialInjectedConditionalValue.of(literal, SocialInjectionParsers.LITERAL, ctx -> {
                switch (condition) {
                    case IS_REPLY -> {
                        final Boolean isReply = ctx.getInjectedValue("is_reply");
                        return Boolean.TRUE.equals(isReply);
                    }
                    default -> {
                        return true;
                    }
                }
            });
        }

        public enum Type {
            LITERAL,
            SPACE
        }

        public enum Condition {
            IS_REPLY
        }

    }

}
