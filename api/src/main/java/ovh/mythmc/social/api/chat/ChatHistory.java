package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class ChatHistory {

    private final Map<Integer, SocialMessageContext> messages = new HashMap<>();

    public int register(final @NotNull SocialMessageContext messageContext) {
        int id = messages.size() + 1;
        messages.put(id, messageContext.withDate(new Date()));
        return id;
    }

    public int getIdByContext(final @NotNull SocialMessageContext messageContext) {
        List<Map.Entry<Integer, SocialMessageContext>> entry = messages.entrySet().stream().filter(e -> e.getValue().equals(messageContext)).toList();
        if (entry.isEmpty())
            return 0;

        return entry.get(0).getKey();
    }

    public SocialMessageContext getById(final @NotNull Integer id) {
        return messages.get(id);
    }

    public List<SocialMessageContext> get() {
        return messages.values().stream().toList();
    }

    public List<SocialMessageContext> getByChannel(final @NotNull ChatChannel chatChannel) {
        return messages.values().stream()
                .filter(message -> message.chatChannel().equals(chatChannel))
                .toList();
    }

    public List<SocialMessageContext> getByPlayer(final @NotNull SocialPlayer socialPlayer) {
        return messages.values().stream()
                .filter(message -> message.sender().equals(socialPlayer))
                .toList();
    }

    public List<SocialMessageContext> getByText(final @NotNull String text) {
        return messages.values().stream()
                .filter(message -> message.rawMessage().contains(text))
                .toList();
    }

    public List<SocialMessageContext> getThread(final @NotNull SocialMessageContext messageContext, int limit) {
        return messages.values().stream()
                .filter(value ->
                        (value.isReply() && value.replyId().equals(messageContext.id())) ||
                        (value.isReply() && value.replyId().equals(messageContext.replyId())) ||
                        value.id().equals(messageContext.id()) ||
                        value.id().equals(messageContext.replyId())
                )
                .limit(limit)
                .toList();
    }

    public boolean isThread(final @NotNull SocialMessageContext messageContext) {
        return getThread(messageContext, 3).size() > 2;
    }

}
