package ovh.mythmc.social.api.chat.channel;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.format.ChatFormatBuilder;
import ovh.mythmc.social.api.chat.renderer.feature.ChatRendererFeature;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.api.util.Mutable;
import ovh.mythmc.social.api.util.registry.RegistryKey;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static net.kyori.adventure.text.Component.text;

/**
 * A private chat channel between two participants.
 */
public class PrivateChatChannel extends ChatChannelImpl {

    /**
     * Retrieves an existing private chat channel between the sender and recipient,
     * or creates and registers a new one if it doesn't exist.
     *
     * @param sender    The first participant.
     * @param recipient The second participant.
     * @return The private chat channel.
     */
    public static @NotNull PrivateChatChannel getOrCreate(final @NotNull SocialUser sender,
            final @NotNull SocialUser recipient) {
        final var channels = Social.registries().channels().values();

        PrivateChatChannel privateChatChannel = channels.stream()
                .filter(PrivateChatChannel.class::isInstance)
                .map(PrivateChatChannel.class::cast)
                .filter(channel -> channel.isBetween(sender, recipient))
                .findFirst()
                .orElse(null);

        if (privateChatChannel == null) {
            privateChatChannel = new PrivateChatChannel(sender, recipient);
            Social.registries().channels().register(RegistryKey.identified(privateChatChannel.name()),
                    privateChatChannel);
        }

        return privateChatChannel;
    }

    private final SocialUser participant1;
    private final SocialUser participant2;

    private PrivateChatChannel(@NotNull SocialUser sender, @NotNull SocialUser recipient) {
        super(
            "PM-" + sender.username() + "-" + recipient.username(),
            Mutable.of("PM"),
            List.of(),
            formattedPmIcon(),
            hoverText(),
            NamedTextColor.GREEN,
            getFormatBuilder(),
            Optional.empty(),
            Optional.of(NamedTextColor.WHITE),
            false,
            List.of()
        );
        this.participant1 = sender;
        this.participant2 = recipient;
        this.addMember(sender);
        this.addMember(recipient);
    }

    /**
     * Gets the first participant of the private channel.
     *
     * @return The first participant.
     */
    public @NotNull SocialUser getParticipant1() {
        return participant1;
    }

    /**
     * Gets the second participant of the private channel.
     *
     * @return The second participant.
     */
    public @NotNull SocialUser getParticipant2() {
        return participant2;
    }

    /**
     * Checks if this private channel is between the two specified users.
     *
     * @param user1 The first user.
     * @param user2 The second user.
     * @return True if the channel is between user1 and user2.
     */
    public boolean isBetween(@NotNull SocialUser user1, @NotNull SocialUser user2) {
        return (participant1.uuid().equals(user1.uuid()) && participant2.uuid().equals(user2.uuid()))
                || (participant1.uuid().equals(user2.uuid()) && participant2.uuid().equals(user1.uuid()));
    }

    @Override
    public @NotNull Collection<ChatRendererFeature> supportedRendererFeatures() {
        return List.of(
                ChatRendererFeature.builder((target, format, message, parser) -> {
                    final TextComponent senderFormattedNickname = (TextComponent) Social.get().getTextProcessor()
                            .parse(message.sender(), message.channel(), "$(formatted_nickname)");
                    final TextComponent recipientFormattedNickname = (TextComponent) Social.get().getTextProcessor()
                            .parse(getRecipientForSender(message.sender()), message.channel(), "$(formatted_nickname)");

                    format.injectValue(SocialInjectedValue.placeholder("sender", senderFormattedNickname));
                    format.injectValue(SocialInjectedValue.placeholder("recipient", recipientFormattedNickname));

                }).build());
    }

    /**
     * Gets the recipient for a given sender in this private channel.
     *
     * @param user The sender.
     * @return The recipient.
     */
    @ApiStatus.Internal
    public @NotNull SocialUser getRecipientForSender(@NotNull SocialUser user) {
        if (participant1.uuid().equals(user.uuid()))
            return participant2;

        return participant1;
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

    private static ChatFormatBuilder getFormatBuilder() {
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
                .injectValue(SocialInjectedValue.placeholder("channel_text_divider",
                        text(divider())))
                .injectValue(SocialInjectedValue.placeholder("channel_text_color", text("white")));
    }

}

