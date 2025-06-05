package ovh.mythmc.social.api.chat.channel;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.format.ChatFormatBuilder;
import ovh.mythmc.social.api.chat.renderer.feature.ChatRendererFeature;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.util.Mutable;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ChatChannel {

    static ChatChannelImpl.ChatChannelBuilder builder(@NotNull String name, @NotNull ChatFormatBuilder formatBuilder) {
        return new ChatChannelImpl.ChatChannelBuilder(name, formatBuilder);
    }

    @NotNull String name();

    @NotNull Mutable<String> alias();

    default String aliasOrName() {
        return alias().isPresent() ? alias().get() : name();
    }

    @NotNull Iterable<String> commands();

    @NotNull Component icon();

    @NotNull Component description();

    @NotNull TextColor color();

    @NotNull Optional<String> permission();

    boolean joinByDefault();

    @NotNull Iterable<ChatRendererFeature> supportedRendererFeatures();

    @NotNull Component prefix(@NotNull AbstractSocialUser user, @NotNull SocialRegisteredMessageContext message, @NotNull SocialParserContext parser);

    boolean addMember(@NotNull UUID uuid);

    default boolean addMember(@NotNull AbstractSocialUser user) {
        return addMember(user.uuid());
    }

    boolean removeMember(@NotNull UUID uuid);

    default boolean removeMember(@NotNull AbstractSocialUser user) {
        return removeMember(user.uuid());
    }

    @NotNull Collection<AbstractSocialUser> members();

    boolean isMember(@NotNull UUID uuid);

    default boolean isMember(@NotNull AbstractSocialUser user) {
        return isMember(user.uuid());
    }

    enum ChannelType {

        CHAT,
        ACTION_BAR

    }
}
