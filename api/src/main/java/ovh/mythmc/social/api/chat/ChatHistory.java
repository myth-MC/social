package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialHistoryMessageContext;
import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.users.SocialUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class ChatHistory {

    private final Map<Integer, SocialHistoryMessageContext> messages = new HashMap<>();

    public int register(final @NotNull SocialMessageContext messageContext, final @NotNull Component finalMessage) {
        int id = messages.size();
        
        SocialHistoryMessageContext historyContext = new SocialHistoryMessageContext(
            id, 
            System.currentTimeMillis(),
            messageContext.sender(), 
            messageContext.chatChannel(), 
            finalMessage, 
            messageContext.rawMessage(), 
            messageContext.replyId(),
            messageContext.signedMessage());

        messages.put(id, historyContext);
        return id;
    }

    public SocialHistoryMessageContext getById(final @NotNull Integer id) {
        return messages.get(id);
    }

    public boolean canDelete(SocialMessageContext context) {
        if (!Bukkit.getOnlineMode()) // Offline servers don't support message deletion
            return false;

        if (context.signedMessage() == null)
            return false;

        return context.signedMessage().canDelete();
    }

    public void delete(SocialHistoryMessageContext context) {
        Social.get().getUserManager().get().forEach(user -> user.deleteMessage(context.signedMessage()));

        SocialHistoryMessageContext newContext = new SocialHistoryMessageContext(
            context.id(), 
            context.timestamp(),
            context.sender(), 
            context.chatChannel(), 
            Component.text("N/A", NamedTextColor.RED), 
            context.rawMessage(), 
            context.replyId(), 
            context.signedMessage());

        messages.put(context.id(), newContext);
    }

    public List<SocialHistoryMessageContext> get() {
        return messages.values().stream().toList();
    }

    public List<SocialHistoryMessageContext> getByChannel(final @NotNull ChatChannel chatChannel) {
        return messages.values().stream()
                .filter(message -> message.chatChannel().equals(chatChannel))
                .toList();
    }

    public List<SocialHistoryMessageContext> getByUser(final @NotNull SocialUser user) {
        return messages.values().stream()
                .filter(message -> message.sender().getUuid().equals(user.getUuid()))
                .toList();
    }

    public List<SocialHistoryMessageContext> getByText(final @NotNull String text) {
        return messages.values().stream()
                .filter(message -> message.rawMessage().contains(text))
                .toList();
    }

    public List<SocialHistoryMessageContext> getThread(final @NotNull SocialHistoryMessageContext messageContext, int limit) {
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

    @Deprecated
    @ScheduledForRemoval // replies and threads are the same thing since v0.3
    public boolean isThread(final @NotNull SocialHistoryMessageContext messageContext) {
        return getThread(messageContext, 3).size() > 2;
    }

}
