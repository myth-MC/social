package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.events.chat.SocialChatMessageSendEvent;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class ChatHistory {

    private final Map<Integer, SocialChatMessageSendEvent> messages = new HashMap<>();

    public int register(final @NotNull SocialChatMessageSendEvent socialChatMessageSendEvent) {
        int id = messages.size() + 1;
        messages.put(id, socialChatMessageSendEvent);
        return id;
    }

    public int getIdByEvent(final @NotNull SocialChatMessageSendEvent socialChatMessageSendEvent) {
        List<Map.Entry<Integer, SocialChatMessageSendEvent>> entry = messages.entrySet().stream().filter(e -> e.getValue().equals(socialChatMessageSendEvent)).toList();
        if (entry.isEmpty())
            return 0;

        return entry.get(0).getKey();
    }

    public SocialChatMessageSendEvent getById(final @NotNull Integer id) {
        return messages.get(id);
    }

    public List<SocialChatMessageSendEvent> getByPlayer(final @NotNull SocialPlayer socialPlayer) {
        return messages.values().stream()
                .filter(event -> event.getSender().equals(socialPlayer))
                .toList();
    }

    public List<SocialChatMessageSendEvent> getByText(final @NotNull String text) {
        return messages.values().stream()
                .filter(event -> event.getRawMessage().contains(text))
                .toList();
    }

    public List<SocialChatMessageSendEvent> getThread(final @NotNull SocialChatMessageSendEvent socialChatMessageSendEvent, int limit) {
        return messages.values().stream()
                .filter(event ->
                        (event.isReply() && event.getReplyId().equals(socialChatMessageSendEvent.getId())) ||
                        (event.isReply() && event.getReplyId().equals(socialChatMessageSendEvent.getReplyId())) ||
                        event.getId().equals(socialChatMessageSendEvent.getId()) ||
                        event.getId().equals(socialChatMessageSendEvent.getReplyId())
                )
                .limit(limit)
                .toList();
    }

    public boolean isThread(final @NotNull SocialChatMessageSendEvent socialChatMessageSendEvent) {
        return getThread(socialChatMessageSendEvent, 3).size() > 2;
    }

}
