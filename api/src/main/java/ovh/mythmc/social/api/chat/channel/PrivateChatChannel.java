package ovh.mythmc.social.api.chat.channel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.format.ChatFormatBuilder;
import ovh.mythmc.social.api.chat.renderer.feature.ChatRendererFeature;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.util.Mutable;
import ovh.mythmc.social.api.util.registry.RegistryKey;

import java.util.Collection;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

@Getter
@Setter(AccessLevel.PROTECTED)
public class PrivateChatChannel extends ChatChannelImpl {

    public static PrivateChatChannel getOrCreate(final @NotNull AbstractSocialUser sender, final @NotNull AbstractSocialUser recipient) {
        final var channels = Social.registries().channels().values();

        PrivateChatChannel privateChatChannel = channels.stream()
            .filter(channel -> channel instanceof PrivateChatChannel)
            .map(channel -> (PrivateChatChannel) channel)
            .filter(channel -> {
                if (channel.participant1.uuid().equals(sender.uuid()) && channel.participant2.uuid().equals(recipient.uuid()))
                    return true;

                if (channel.participant1.uuid().equals(recipient.uuid()) && channel.participant2.uuid().equals(sender.uuid()))
                    return true;

                return false;
            })
            .findFirst().orElse(null);

        if (privateChatChannel == null) { // Register if null
            privateChatChannel = new PrivateChatChannel(sender, recipient);
            Social.registries().channels().register(RegistryKey.identified(privateChatChannel.name()), privateChatChannel);
        }

        return privateChatChannel;
    }

    private final AbstractSocialUser participant1;

    private final AbstractSocialUser participant2;

    private PrivateChatChannel(@NotNull AbstractSocialUser sender, @NotNull AbstractSocialUser recipient) {
        super("PM-" + sender.name() + "-" + recipient.name(), Mutable.of("PM"), formattedPmIcon(), hoverText(), NamedTextColor.GREEN, ChatFormatBuilder.empty(), null, false, List.of());
        this.participant1 = sender;
        this.participant2 = recipient;
        this.addMember(sender);
        this.addMember(recipient);
    }

    @Override
    protected ChatFormatBuilder formatBuilder() {
        return ChatFormatBuilder.empty()
            .append(text("$(channel_icon)"))
            .appendSpace()
            .append(text("$(sender)"))
            .appendSpace()
            .append(text("$(channel_text_divider)"))
            .appendSpace()
            .append(text("$(recipient)"))
            .append(text(":"))
            .appendSpace()
            .append(text("<$(channel_text_color)>"))
            .injectValue(SocialInjectedValue.placeholder("channel_text_divider", text(Social.get().getConfig().getCommands().getPrivateMessage().arrow())))
            .injectValue(SocialInjectedValue.placeholder("channel_text_color", text("white")));
    }

    @Override
    public @NotNull Collection<ChatRendererFeature> supportedRendererFeatures() {
        return List.of(
            ChatRendererFeature.builder((target, format, message, parser) -> {
                final TextComponent senderFormattedNickname = (TextComponent) Social.get().getTextProcessor().parse(message.sender(), message.channel(), "$(formatted_nickname)");
                final TextComponent recipientFormattedNickname = (TextComponent) Social.get().getTextProcessor().parse(getRecipientForSender(message.sender()), message.channel(), "$(formatted_nickname)");

                format.injectValue(SocialInjectedValue.placeholder("sender", senderFormattedNickname));
                format.injectValue(SocialInjectedValue.placeholder("recipient", recipientFormattedNickname));

            }).build()
        );
    }

    @ApiStatus.Internal
    public AbstractSocialUser getRecipientForSender(@NotNull AbstractSocialUser user) {
        if (participant1.uuid().equals(user.uuid()))
            return participant2;

        return participant1;
    }

    private static String alias(@NotNull AbstractSocialUser sender, @NotNull AbstractSocialUser recipient) {
        return "PM";
    }

    private static Component formattedPmIcon() {
        return text(Social.get().getConfig().getCommands().getPrivateMessage().prefix());
    }

    private static String divider() {
        return Social.get().getConfig().getCommands().getPrivateMessage().arrow();
    }

    private static Component hoverText() {
        return text(Social.get().getConfig().getCommands().getPrivateMessage().hoverText());
    }

}
