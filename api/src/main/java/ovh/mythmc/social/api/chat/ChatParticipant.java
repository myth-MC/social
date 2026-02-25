package ovh.mythmc.social.api.chat;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;
import ovh.mythmc.social.api.util.Mutable;

public interface ChatParticipant {

    @NotNull
    Mutable<ChatChannel> mainChannel();

    @NotNull
    Optional<GroupChatChannel> groupChannel();

    @NotNull
    Set<String> blockedChannels();

    @NotNull
    Mutable<UUID> lastPrivateMessageRecipient();

}
